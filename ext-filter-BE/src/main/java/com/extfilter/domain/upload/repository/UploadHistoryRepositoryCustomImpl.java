package com.extfilter.domain.upload.repository;

import static com.extfilter.domain.upload.entity.QUploadHistory.uploadHistory;

import com.extfilter.domain.upload.dto.DailyUploadDto;
import com.extfilter.domain.upload.dto.ExtensionCountDto;
import com.extfilter.domain.upload.entity.UploadHistory;
import com.extfilter.domain.upload.entity.UploadStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class UploadHistoryRepositoryCustomImpl implements UploadHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UploadHistory> findHistoryWithFilters(
            UploadStatus status,
            String extension,
            Pageable pageable
    ) {
        JPAQuery<UploadHistory> query = queryFactory
                .selectFrom(uploadHistory)
                .where(
                        statusEq(status),
                        extensionEq(extension)
                )
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<UploadHistory> content = query.fetch();

        Long total = queryFactory
                .select(uploadHistory.count())
                .from(uploadHistory)
                .where(
                        statusEq(status),
                        extensionEq(extension)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public List<ExtensionCountDto> findTopBlockedExtensions(
            UploadStatus status,
            int limit
    ) {
        return queryFactory
                .select(Projections.constructor(
                        ExtensionCountDto.class,
                        uploadHistory.fileExtension,
                        uploadHistory.count()
                ))
                .from(uploadHistory)
                .where(uploadHistory.uploadStatus.eq(status))
                .groupBy(uploadHistory.fileExtension)
                .orderBy(uploadHistory.count().desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<DailyUploadDto> findDailyUploadTrend(LocalDateTime startDate) {
        return queryFactory
                .select(Projections.constructor(
                        DailyUploadDto.class,
                        uploadHistory.createdAt.min(),
                        uploadHistory.count(),
                        Expressions.numberTemplate(Long.class,
                                "SUM(CASE WHEN {0} = 'SUCCESS' THEN 1 ELSE 0 END)",
                                uploadHistory.uploadStatus),
                        Expressions.numberTemplate(Long.class,
                                "SUM(CASE WHEN {0} = 'BLOCKED' THEN 1 ELSE 0 END)",
                                uploadHistory.uploadStatus)
                ))
                .from(uploadHistory)
                .where(uploadHistory.createdAt.goe(startDate))
                .groupBy(uploadHistory.createdAt.year(), uploadHistory.createdAt.month(),
                        uploadHistory.createdAt.dayOfMonth())
                .orderBy(uploadHistory.createdAt.year().desc(), uploadHistory.createdAt.month().desc(),
                        uploadHistory.createdAt.dayOfMonth().desc())
                .fetch();
    }

    private BooleanExpression statusEq(UploadStatus status) {
        return status != null ? uploadHistory.uploadStatus.eq(status) : null;
    }

    private BooleanExpression extensionEq(String extension) {
        return extension != null ? uploadHistory.fileExtension.eq(extension) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    Sort.Direction direction = order.getDirection();
                    String property = order.getProperty();

                    return switch (property) {
                        case "createdAt" -> direction.isAscending()
                                ? uploadHistory.createdAt.asc()
                                : uploadHistory.createdAt.desc();
                        case "fileSize" -> direction.isAscending()
                                ? uploadHistory.fileSize.asc()
                                : uploadHistory.fileSize.desc();
                        default -> uploadHistory.createdAt.desc();
                    };
                })
                .toArray(OrderSpecifier[]::new);
    }
}

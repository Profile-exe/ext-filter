-- ============================================================
-- 샘플 데이터 삽입 (개발/테스트 전용)
-- DEV ONLY: This migration is for development and testing purposes
--
-- Reset command: docker compose down -v && docker compose up -d && sleep 10
--
-- Contents:
-- 1. 5 custom extensions (zip, rar, dmg, apk, msi)
-- 2. 80 upload history records (60 SUCCESS, 20 BLOCKED) across 14 days
-- ============================================================

-- ============================================================
-- 1. 커스텀 확장자 (5개)
-- ============================================================
INSERT INTO custom_extensions (extension_name, created_at, updated_at) VALUES
    ('zip', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('rar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('dmg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('apk', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('msi', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- 2. 업로드 이력 (80개: SUCCESS 60개 + BLOCKED 20개)
-- ============================================================

INSERT INTO upload_history (original_filename, file_extension, file_size, upload_status, stored_filename, created_at, updated_at) VALUES
    -- ============================================================
    -- SUCCESS 케이스 (60개) - stored_filename 포함
    -- ============================================================

    -- PDF 파일 (15개) - 일반 문서
    ('2024_annual_report.pdf', 'pdf', 523000, 'SUCCESS', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890_2024_annual_report.pdf', CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours 30 minutes'),
    ('meeting_notes_dec.pdf', 'pdf', 245000, 'SUCCESS', 'b2c3d4e5-f6a7-8901-bcde-f12345678901_meeting_notes_dec.pdf', CURRENT_TIMESTAMP - INTERVAL '1 day 10 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day 10 hours 15 minutes'),
    ('project_proposal.pdf', 'pdf', 678000, 'SUCCESS', 'c3d4e5f6-a7b8-9012-cdef-234567890abc_project_proposal.pdf', CURRENT_TIMESTAMP - INTERVAL '2 days 3 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 3 hours 45 minutes'),
    ('invoice_12345.pdf', 'pdf', 156000, 'SUCCESS', 'd4e5f6a7-b8c9-0123-def0-34567890abcd_invoice_12345.pdf', CURRENT_TIMESTAMP - INTERVAL '2 days 8 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 8 hours 20 minutes'),
    ('user_manual.pdf', 'pdf', 892000, 'SUCCESS', 'e5f6a7b8-c9d0-1234-ef01-4567890abcde_user_manual.pdf', CURRENT_TIMESTAMP - INTERVAL '3 days 2 hours 10 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days 2 hours 10 minutes'),
    ('budget_2025.pdf', 'pdf', 334000, 'SUCCESS', 'f6a7b8c9-d0e1-2345-f012-567890abcdef_budget_2025.pdf', CURRENT_TIMESTAMP - INTERVAL '3 days 6 hours 55 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days 6 hours 55 minutes'),
    ('presentation_slides.pdf', 'pdf', 1245000, 'SUCCESS', '01234567-89ab-cdef-0123-456789abcdef_presentation_slides.pdf', CURRENT_TIMESTAMP - INTERVAL '4 days 4 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '4 days 4 hours 30 minutes'),
    ('contract_agreement.pdf', 'pdf', 423000, 'SUCCESS', '12345678-9abc-def0-1234-56789abcdef0_contract_agreement.pdf', CURRENT_TIMESTAMP - INTERVAL '5 days 1 hour 15 minutes', CURRENT_TIMESTAMP - INTERVAL '5 days 1 hour 15 minutes'),
    ('technical_spec.pdf', 'pdf', 756000, 'SUCCESS', '23456789-abcd-ef01-2345-6789abcdef01_technical_spec.pdf', CURRENT_TIMESTAMP - INTERVAL '5 days 9 hours 40 minutes', CURRENT_TIMESTAMP - INTERVAL '5 days 9 hours 40 minutes'),
    ('marketing_plan.pdf', 'pdf', 567000, 'SUCCESS', '3456789a-bcde-f012-3456-789abcdef012_marketing_plan.pdf', CURRENT_TIMESTAMP - INTERVAL '6 days 3 hours 25 minutes', CURRENT_TIMESTAMP - INTERVAL '6 days 3 hours 25 minutes'),
    ('training_materials.pdf', 'pdf', 934000, 'SUCCESS', '456789ab-cdef-0123-4567-89abcdef0123_training_materials.pdf', CURRENT_TIMESTAMP - INTERVAL '7 days 7 hours 50 minutes', CURRENT_TIMESTAMP - INTERVAL '7 days 7 hours 50 minutes'),
    ('quarterly_review.pdf', 'pdf', 445000, 'SUCCESS', '56789abc-def0-1234-5678-9abcdef01234_quarterly_review.pdf', CURRENT_TIMESTAMP - INTERVAL '8 days 2 hours 35 minutes', CURRENT_TIMESTAMP - INTERVAL '8 days 2 hours 35 minutes'),
    ('company_policy.pdf', 'pdf', 312000, 'SUCCESS', '6789abcd-ef01-2345-6789-abcdef012345_company_policy.pdf', CURRENT_TIMESTAMP - INTERVAL '10 days 5 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '10 days 5 hours 20 minutes'),
    ('research_paper.pdf', 'pdf', 823000, 'SUCCESS', '789abcde-f012-3456-789a-bcdef0123456_research_paper.pdf', CURRENT_TIMESTAMP - INTERVAL '12 days 8 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '12 days 8 hours 45 minutes'),
    ('feasibility_study.pdf', 'pdf', 634000, 'SUCCESS', '89abcdef-0123-4567-89ab-cdef01234567_feasibility_study.pdf', CURRENT_TIMESTAMP - INTERVAL '13 days 4 hours 10 minutes', CURRENT_TIMESTAMP - INTERVAL '13 days 4 hours 10 minutes'),

    -- JPG 이미지 (12개)
    ('profile_photo.jpg', 'jpg', 87500, 'SUCCESS', '9abcdef0-1234-5678-9abc-def012345678_profile_photo.jpg', CURRENT_TIMESTAMP - INTERVAL '1 day 7 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day 7 hours 20 minutes'),
    ('team_meeting.jpg', 'jpg', 124000, 'SUCCESS', 'abcdef01-2345-6789-abcd-ef0123456789_team_meeting.jpg', CURRENT_TIMESTAMP - INTERVAL '1 day 11 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day 11 hours 45 minutes'),
    ('product_sample.jpg', 'jpg', 95600, 'SUCCESS', 'bcdef012-3456-789a-bcde-f01234567890_product_sample.jpg', CURRENT_TIMESTAMP - INTERVAL '2 days 5 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 5 hours 30 minutes'),
    ('office_interior.jpg', 'jpg', 78300, 'SUCCESS', 'cdef0123-4567-89ab-cdef-012345678901_office_interior.jpg', CURRENT_TIMESTAMP - INTERVAL '2 days 9 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 9 hours 15 minutes'),
    ('conference_photo.jpg', 'jpg', 134000, 'SUCCESS', 'def01234-5678-9abc-def0-123456789012_conference_photo.jpg', CURRENT_TIMESTAMP - INTERVAL '3 days 4 hours 50 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days 4 hours 50 minutes'),
    ('whiteboard_sketch.jpg', 'jpg', 56700, 'SUCCESS', 'ef012345-6789-abcd-ef01-234567890123_whiteboard_sketch.jpg', CURRENT_TIMESTAMP - INTERVAL '4 days 2 hours 25 minutes', CURRENT_TIMESTAMP - INTERVAL '4 days 2 hours 25 minutes'),
    ('chart_diagram.jpg', 'jpg', 89400, 'SUCCESS', 'f0123456-789a-bcde-f012-345678901234_chart_diagram.jpg', CURRENT_TIMESTAMP - INTERVAL '5 days 6 hours 40 minutes', CURRENT_TIMESTAMP - INTERVAL '5 days 6 hours 40 minutes'),
    ('receipt_scan.jpg', 'jpg', 45200, 'SUCCESS', '01234567-89ab-cdef-0123-456789abcdef_receipt_scan.jpg', CURRENT_TIMESTAMP - INTERVAL '6 days 8 hours 55 minutes', CURRENT_TIMESTAMP - INTERVAL '6 days 8 hours 55 minutes'),
    ('logo_design.jpg', 'jpg', 112000, 'SUCCESS', '12345678-9abc-def0-1234-56789abcdef0_logo_design.jpg', CURRENT_TIMESTAMP - INTERVAL '7 days 3 hours 35 minutes', CURRENT_TIMESTAMP - INTERVAL '7 days 3 hours 35 minutes'),
    ('event_photo.jpg', 'jpg', 98700, 'SUCCESS', '23456789-abcd-ef01-2345-6789abcdef01_event_photo.jpg', CURRENT_TIMESTAMP - INTERVAL '9 days 7 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '9 days 7 hours 20 minutes'),
    ('id_card_scan.jpg', 'jpg', 67500, 'SUCCESS', '3456789a-bcde-f012-3456-789abcdef012_id_card_scan.jpg', CURRENT_TIMESTAMP - INTERVAL '11 days 5 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '11 days 5 hours 15 minutes'),
    ('equipment_photo.jpg', 'jpg', 103000, 'SUCCESS', '456789ab-cdef-0123-4567-89abcdef0123_equipment_photo.jpg', CURRENT_TIMESTAMP - INTERVAL '13 days 9 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '13 days 9 hours 30 minutes'),

    -- PNG 이미지 (10개)
    ('screenshot_dashboard.png', 'png', 234000, 'SUCCESS', '56789abc-def0-1234-5678-9abcdef01234_screenshot_dashboard.png', CURRENT_TIMESTAMP - INTERVAL '1 day 6 hours 40 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day 6 hours 40 minutes'),
    ('ui_mockup.png', 'png', 456000, 'SUCCESS', '6789abcd-ef01-2345-6789-abcdef012345_ui_mockup.png', CURRENT_TIMESTAMP - INTERVAL '2 days 4 hours 25 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 4 hours 25 minutes'),
    ('infographic.png', 'png', 567000, 'SUCCESS', '789abcde-f012-3456-789a-bcdef0123456_infographic.png', CURRENT_TIMESTAMP - INTERVAL '3 days 8 hours 10 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days 8 hours 10 minutes'),
    ('company_logo.png', 'png', 89000, 'SUCCESS', '89abcdef-0123-4567-89ab-cdef01234567_company_logo.png', CURRENT_TIMESTAMP - INTERVAL '4 days 5 hours 55 minutes', CURRENT_TIMESTAMP - INTERVAL '4 days 5 hours 55 minutes'),
    ('graph_export.png', 'png', 178000, 'SUCCESS', '9abcdef0-1234-5678-9abc-def012345678_graph_export.png', CURRENT_TIMESTAMP - INTERVAL '5 days 2 hours 35 minutes', CURRENT_TIMESTAMP - INTERVAL '5 days 2 hours 35 minutes'),
    ('diagram_flow.png', 'png', 312000, 'SUCCESS', 'abcdef01-2345-6789-abcd-ef0123456789_diagram_flow.png', CURRENT_TIMESTAMP - INTERVAL '6 days 7 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '6 days 7 hours 15 minutes'),
    ('banner_design.png', 'png', 445000, 'SUCCESS', 'bcdef012-3456-789a-bcde-f01234567890_banner_design.png', CURRENT_TIMESTAMP - INTERVAL '8 days 4 hours 50 minutes', CURRENT_TIMESTAMP - INTERVAL '8 days 4 hours 50 minutes'),
    ('floor_plan.png', 'png', 289000, 'SUCCESS', 'cdef0123-4567-89ab-cdef-012345678901_floor_plan.png', CURRENT_TIMESTAMP - INTERVAL '10 days 6 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '10 days 6 hours 20 minutes'),
    ('wireframe.png', 'png', 123000, 'SUCCESS', 'def01234-5678-9abc-def0-123456789012_wireframe.png', CURRENT_TIMESTAMP - INTERVAL '12 days 3 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '12 days 3 hours 45 minutes'),
    ('icon_set.png', 'png', 267000, 'SUCCESS', 'ef012345-6789-abcd-ef01-234567890123_icon_set.png', CURRENT_TIMESTAMP - INTERVAL '13 days 7 hours 25 minutes', CURRENT_TIMESTAMP - INTERVAL '13 days 7 hours 25 minutes'),

    -- TXT 파일 (8개)
    ('notes.txt', 'txt', 12500, 'SUCCESS', 'f0123456-789a-bcde-f012-345678901234_notes.txt', CURRENT_TIMESTAMP - INTERVAL '1 day 9 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day 9 hours 30 minutes'),
    ('todo_list.txt', 'txt', 8900, 'SUCCESS', '01234567-89ab-cdef-0123-456789abcdef_todo_list.txt', CURRENT_TIMESTAMP - INTERVAL '2 days 6 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 6 hours 15 minutes'),
    ('readme.txt', 'txt', 15600, 'SUCCESS', '12345678-9abc-def0-1234-56789abcdef0_readme.txt', CURRENT_TIMESTAMP - INTERVAL '3 days 7 hours 50 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days 7 hours 50 minutes'),
    ('changelog.txt', 'txt', 23400, 'SUCCESS', '23456789-abcd-ef01-2345-6789abcdef01_changelog.txt', CURRENT_TIMESTAMP - INTERVAL '5 days 4 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '5 days 4 hours 20 minutes'),
    ('requirements.txt', 'txt', 18700, 'SUCCESS', '3456789a-bcde-f012-3456-789abcdef012_requirements.txt', CURRENT_TIMESTAMP - INTERVAL '7 days 2 hours 40 minutes', CURRENT_TIMESTAMP - INTERVAL '7 days 2 hours 40 minutes'),
    ('config_sample.txt', 'txt', 11200, 'SUCCESS', '456789ab-cdef-0123-4567-89abcdef0123_config_sample.txt', CURRENT_TIMESTAMP - INTERVAL '9 days 8 hours 55 minutes', CURRENT_TIMESTAMP - INTERVAL '9 days 8 hours 55 minutes'),
    ('instructions.txt', 'txt', 19800, 'SUCCESS', '56789abc-def0-1234-5678-9abcdef01234_instructions.txt', CURRENT_TIMESTAMP - INTERVAL '11 days 6 hours 10 minutes', CURRENT_TIMESTAMP - INTERVAL '11 days 6 hours 10 minutes'),
    ('license.txt', 'txt', 14300, 'SUCCESS', '6789abcd-ef01-2345-6789-abcdef012345_license.txt', CURRENT_TIMESTAMP - INTERVAL '13 days 3 hours 35 minutes', CURRENT_TIMESTAMP - INTERVAL '13 days 3 hours 35 minutes'),

    -- DOCX 파일 (7개)
    ('proposal_draft.docx', 'docx', 456000, 'SUCCESS', '789abcde-f012-3456-789a-bcdef0123456_proposal_draft.docx', CURRENT_TIMESTAMP - INTERVAL '1 day 8 hours 25 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day 8 hours 25 minutes'),
    ('meeting_agenda.docx', 'docx', 234000, 'SUCCESS', '89abcdef-0123-4567-89ab-cdef01234567_meeting_agenda.docx', CURRENT_TIMESTAMP - INTERVAL '2 days 7 hours 10 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 7 hours 10 minutes'),
    ('employee_handbook.docx', 'docx', 789000, 'SUCCESS', '9abcdef0-1234-5678-9abc-def012345678_employee_handbook.docx', CURRENT_TIMESTAMP - INTERVAL '4 days 3 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '4 days 3 hours 45 minutes'),
    ('project_summary.docx', 'docx', 345000, 'SUCCESS', 'abcdef01-2345-6789-abcd-ef0123456789_project_summary.docx', CURRENT_TIMESTAMP - INTERVAL '6 days 5 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '6 days 5 hours 30 minutes'),
    ('cover_letter.docx', 'docx', 123000, 'SUCCESS', 'bcdef012-3456-789a-bcde-f01234567890_cover_letter.docx', CURRENT_TIMESTAMP - INTERVAL '8 days 7 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '8 days 7 hours 15 minutes'),
    ('resume_updated.docx', 'docx', 267000, 'SUCCESS', 'cdef0123-4567-89ab-cdef-012345678901_resume_updated.docx', CURRENT_TIMESTAMP - INTERVAL '10 days 4 hours 50 minutes', CURRENT_TIMESTAMP - INTERVAL '10 days 4 hours 50 minutes'),
    ('report_final.docx', 'docx', 512000, 'SUCCESS', 'def01234-5678-9abc-def0-123456789012_report_final.docx', CURRENT_TIMESTAMP - INTERVAL '12 days 6 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '12 days 6 hours 20 minutes'),

    -- XLSX 파일 (5개)
    ('financial_data.xlsx', 'xlsx', 1234000, 'SUCCESS', 'ef012345-6789-abcd-ef01-234567890123_financial_data.xlsx', CURRENT_TIMESTAMP - INTERVAL '2 days 2 hours 35 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 2 hours 35 minutes'),
    ('inventory_list.xlsx', 'xlsx', 876000, 'SUCCESS', 'f0123456-789a-bcde-f012-345678901234_inventory_list.xlsx', CURRENT_TIMESTAMP - INTERVAL '4 days 6 hours 40 minutes', CURRENT_TIMESTAMP - INTERVAL '4 days 6 hours 40 minutes'),
    ('sales_report_q4.xlsx', 'xlsx', 1567000, 'SUCCESS', '01234567-89ab-cdef-0123-456789abcdef_sales_report_q4.xlsx', CURRENT_TIMESTAMP - INTERVAL '7 days 5 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '7 days 5 hours 20 minutes'),
    ('employee_roster.xlsx', 'xlsx', 654000, 'SUCCESS', '12345678-9abc-def0-1234-56789abcdef0_employee_roster.xlsx', CURRENT_TIMESTAMP - INTERVAL '10 days 8 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '10 days 8 hours 45 minutes'),
    ('budget_tracker.xlsx', 'xlsx', 945000, 'SUCCESS', '23456789-abcd-ef01-2345-6789abcdef01_budget_tracker.xlsx', CURRENT_TIMESTAMP - INTERVAL '13 days 2 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '13 days 2 hours 15 minutes'),

    -- MP4 비디오 (3개)
    ('tutorial_video.mp4', 'mp4', 8456000, 'SUCCESS', '3456789a-bcde-f012-3456-789abcdef012_tutorial_video.mp4', CURRENT_TIMESTAMP - INTERVAL '3 days 9 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days 9 hours 30 minutes'),
    ('product_demo.mp4', 'mp4', 6234000, 'SUCCESS', '456789ab-cdef-0123-4567-89abcdef0123_product_demo.mp4', CURRENT_TIMESTAMP - INTERVAL '8 days 6 hours 25 minutes', CURRENT_TIMESTAMP - INTERVAL '8 days 6 hours 25 minutes'),
    ('webinar_recording.mp4', 'mp4', 9876000, 'SUCCESS', '56789abc-def0-1234-5678-9abcdef01234_webinar_recording.mp4', CURRENT_TIMESTAMP - INTERVAL '11 days 4 hours 40 minutes', CURRENT_TIMESTAMP - INTERVAL '11 days 4 hours 40 minutes'),

    -- ============================================================
    -- BLOCKED 케이스 (20개) - stored_filename은 NULL
    -- ============================================================

    -- EXE 파일 (4개) - 가장 위험한 확장자
    ('installer_setup.exe', 'exe', 5240000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours 15 minutes'),
    ('game_crack.exe', 'exe', 3450000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '2 days 7 hours 50 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 7 hours 50 minutes'),
    ('malware_sample.exe', 'exe', 1890000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '5 days 5 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '5 days 5 hours 20 minutes'),
    ('trojan_virus.exe', 'exe', 2340000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '9 days 8 hours 40 minutes', CURRENT_TIMESTAMP - INTERVAL '9 days 8 hours 40 minutes'),

    -- BAT 파일 (3개)
    ('autorun.bat', 'bat', 45000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '1 day 11 hours 25 minutes', CURRENT_TIMESTAMP - INTERVAL '1 day 11 hours 25 minutes'),
    ('system_clean.bat', 'bat', 23000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '4 days 6 hours 35 minutes', CURRENT_TIMESTAMP - INTERVAL '4 days 6 hours 35 minutes'),
    ('deploy_script.bat', 'bat', 34000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '10 days 9 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '10 days 9 hours 15 minutes'),

    -- ZIP 파일 (2개) - 커스텀 차단
    ('suspicious_archive.zip', 'zip', 3200000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '2 days 4 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '2 days 4 hours 45 minutes'),
    ('payload.zip', 'zip', 1567000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '7 days 7 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '7 days 7 hours 30 minutes'),

    -- JS 파일 (2개)
    ('malicious_script.js', 'js', 67000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '3 days 5 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '3 days 5 hours 20 minutes'),
    ('crypto_miner.js', 'js', 89000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '8 days 3 hours 55 minutes', CURRENT_TIMESTAMP - INTERVAL '8 days 3 hours 55 minutes'),

    -- MSI 파일 (2개) - 커스텀 차단
    ('software_installer.msi', 'msi', 4560000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '4 days 8 hours 10 minutes', CURRENT_TIMESTAMP - INTERVAL '4 days 8 hours 10 minutes'),
    ('package_setup.msi', 'msi', 3890000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '11 days 5 hours 50 minutes', CURRENT_TIMESTAMP - INTERVAL '11 days 5 hours 50 minutes'),

    -- CMD 파일 (2개)
    ('system_command.cmd', 'cmd', 12000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '5 days 9 hours 40 minutes', CURRENT_TIMESTAMP - INTERVAL '5 days 9 hours 40 minutes'),
    ('registry_edit.cmd', 'cmd', 18000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '12 days 7 hours 25 minutes', CURRENT_TIMESTAMP - INTERVAL '12 days 7 hours 25 minutes'),

    -- 기타 고정 확장자 (COM, SCR, CPL 각 1개)
    ('boot_sector.com', 'com', 34000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '6 days 4 hours 30 minutes', CURRENT_TIMESTAMP - INTERVAL '6 days 4 hours 30 minutes'),
    ('screensaver_virus.scr', 'scr', 1234000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '9 days 6 hours 15 minutes', CURRENT_TIMESTAMP - INTERVAL '9 days 6 hours 15 minutes'),
    ('control_panel.cpl', 'cpl', 567000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '13 days 8 hours 50 minutes', CURRENT_TIMESTAMP - INTERVAL '13 days 8 hours 50 minutes'),

    -- 커스텀 확장자 (APK, DMG 각 1개)
    ('malware_app.apk', 'apk', 2340000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '7 days 3 hours 45 minutes', CURRENT_TIMESTAMP - INTERVAL '7 days 3 hours 45 minutes'),
    ('trojan_installer.dmg', 'dmg', 5670000, 'BLOCKED', NULL, CURRENT_TIMESTAMP - INTERVAL '12 days 9 hours 20 minutes', CURRENT_TIMESTAMP - INTERVAL '12 days 9 hours 20 minutes');

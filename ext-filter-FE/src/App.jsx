import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Navigation from './components/common/Navigation';
import ExtensionManagementPage from './pages/ExtensionManagementPage';
import FileUploadPage from './pages/FileUploadPage';
import UploadHistoryPage from './pages/UploadHistoryPage';
import StatisticsDashboardPage from './pages/StatisticsDashboardPage';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-50">
        <Navigation />
        <Routes>
          <Route path="/" element={<Navigate to="/extensions" replace />} />
          <Route path="/extensions" element={<ExtensionManagementPage />} />
          <Route path="/upload" element={<FileUploadPage />} />
          <Route path="/history" element={<UploadHistoryPage />} />
          <Route path="/statistics" element={<StatisticsDashboardPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;

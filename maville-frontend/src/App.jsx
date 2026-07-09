import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import AppLayout from "./layouts/AppLayout";
import ProtectedRoute from "./auth/ProtectedRoute";

// resident
import ResidentPanel      from "./pages/resident/ResidentPanel";
import ReportProblem      from "./pages/resident/ReportProblem";
import MyReports          from "./pages/resident/MyReports";
import WorksPage          from "./pages/resident/WorksPage";
import SubscriptionsPage  from "./pages/SubscriptionsPage";

// admin
import AdminPanel         from "./pages/admin/AdminPanel";
import { AllSignals }         from "./pages/admin/AllSignals";
import { NotAssigned }        from "./pages/admin/NotAssigned";
import { AssignedProblems }   from "./pages/admin/AssignedProblems";
import SubmittedProposals from "./pages/admin/SubmittedProposals";

// provider
import ProviderPanel      from "./pages/provider/ProviderPanel";
import ProblemsAssigned   from "./pages/provider/ProblemsAssigned";
import CreateProposalPage from "./pages/provider/CreateProposalPage";
import MyWorks       from "./pages/provider/MyWorks";

export default function App() {
  return (
    <Routes>
      {/* PUBLIC */}
      <Route path="/"       element={<Navigate to="/login" replace />} />
      <Route path="/login"  element={<Login />} />

      {/* PROTECTED — sidebar via AppLayout */}
      <Route
        element={
          <ProtectedRoute allowedRoles={["resident", "admin", "provider"]}>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        {/* RESIDENT */}
        <Route path="/resident">
          <Route index                      element={<ResidentPanel />} />
          <Route path="report"              element={<ReportProblem />} />
          <Route path="my-problems"         element={<MyReports />} />
          <Route path="projects"            element={<WorksPage />} />
          <Route path="subscriptions"       element={<SubscriptionsPage />} />
        </Route>

        {/* ADMIN */}
        <Route path="/admin">
          <Route index                      element={<AdminPanel />} />
          <Route path="signals"             element={<AllSignals />} />
          <Route path="not-assigned"        element={<NotAssigned />} />
          <Route path="assigned"            element={<AssignedProblems />} />
          <Route path="submitted-proposals" element={<SubmittedProposals />} />
        </Route>

        {/* PROVIDER */}
        <Route path="/provider">
          <Route index                      element={<ProviderPanel />} />
          <Route path="/provider/assigned-problems" element={<ProblemsAssigned />} />
          <Route path="create-proposal" element={<CreateProposalPage />} />
          <Route path="my-projects" element={<MyWorks />} />   // ✅ add this line
        </Route>
      </Route>
    </Routes>
   ); }
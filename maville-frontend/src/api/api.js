// src/api/api.js
const BASE_URL = 'http://localhost:7070/api';

// ─── Helpers ────────────────────────────────────────────────────────────────

function getHeaders() {
  const headers = { 'Content-Type': 'application/json' };
  const token = localStorage.getItem('token');
  if (token) headers['Authorization'] = `Bearer ${token}`;
  return headers;
}

async function apiFetch(path, options = {}) {
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      ...getHeaders(),
      ...(options.headers || {}),
    },
    credentials: 'include', // sends session cookie set by Spring Security
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`API error ${response.status}: ${errorText}`);
  }

  // 204 No Content or empty body — return null
  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

// ─── Public API ──────────────────────────────────────────────────────────────

export const api = {

  // ── Auth ──────────────────────────────────────────────────────────────────

  login: (data) => apiFetch('/auth/login', { method: 'POST', body: JSON.stringify(data) }),

  registerResident: (data) =>
    apiFetch('/resident/register', { method: 'POST', body: JSON.stringify(data) }),

  registerProvider: (data) =>
    apiFetch('/provider/register', { method: 'POST', body: JSON.stringify(data) }),

  // ── Current user ──────────────────────────────────────────────────────────

  getCurrentUser: () => apiFetch('/users/me'),

  // ── Problems ──────────────────────────────────────────────────────────────

  reportProblem: (data) =>
    apiFetch('/problems/report_problem', { method: 'POST', body: JSON.stringify(data) }),

  getMyProblems: (residentId) => apiFetch(`/problems/my_reported_problems?residentId=${residentId}`),

  getAllProblems: () => apiFetch('/problems/all_reported_problems'),

  getNotAssignedProblems: () => apiFetch('/problems/problems_not_assigned'),

  getAssignedProblems: () => apiFetch('/problems/problems_assigned'),

  assignPriority: (problemId, priorite) =>
    apiFetch(`/problems/assign_problem_priority?problemId=${problemId}&priorite=${priorite}`, {
      method: 'POST',
    }),

  linkSignalToParent: (signalId, parentProblemId) =>
    apiFetch(`/problems/link_signal?signalId=${signalId}&parentProblemId=${parentProblemId}`, {
      method: 'POST',
    }),

  // ── Projects (proposals & work orders) ───────────────────────────────────

  submitProposal: (data) => {
    const params = new URLSearchParams(data).toString();
    return apiFetch(`/projects/submit?${params}`, { method: 'POST' });
  },

  getMyProposals: (providerCompanyNumber) =>
    apiFetch(`/projects/my-proposals?providerCompanyNumber=${encodeURIComponent(providerCompanyNumber)}`),

  updateProposalDescription: (projectId, providerCompanyNumber, newDescription) =>
    apiFetch(
      `/projects/${projectId}/description?providerCompanyNumber=${encodeURIComponent(providerCompanyNumber)}&newDescription=${encodeURIComponent(newDescription)}`,
      { method: 'PUT' }
    ),

  updateProposalEndDate: (projectId, providerCompanyNumber, newEndDate) =>
    apiFetch(
      `/projects/${projectId}/end-date?providerCompanyNumber=${encodeURIComponent(providerCompanyNumber)}&newEndDate=${newEndDate}`,
      { method: 'PUT' }
    ),

  startWork: (projectId, providerCompanyNumber) =>
    apiFetch(
      `/projects/${projectId}/start?providerCompanyNumber=${encodeURIComponent(providerCompanyNumber)}`,
      { method: 'PUT' }
    ),

  delayWork: (projectId, providerCompanyNumber) =>
    apiFetch(
      `/projects/${projectId}/delay?providerCompanyNumber=${encodeURIComponent(providerCompanyNumber)}`,
      { method: 'PUT' }
    ),

  resumeWork: (projectId, providerCompanyNumber) =>
    apiFetch(
      `/projects/${projectId}/resume?providerCompanyNumber=${encodeURIComponent(providerCompanyNumber)}`,
      { method: 'PUT' }
    ),

  completeWork: (projectId, providerCompanyNumber, actualCost) => {
    const costParam = actualCost != null ? `&actualCost=${actualCost}` : '';
    return apiFetch(
      `/projects/${projectId}/complete?providerCompanyNumber=${encodeURIComponent(providerCompanyNumber)}${costParam}`,
      { method: 'PUT' }
    );
  },

  // ── STPM / agent project management ──────────────────────────────────────

  getSubmittedProposals: () => apiFetch('/projects/submitted'),

  acceptProposal: (projectId) => apiFetch(`/projects/${projectId}/accept`, { method: 'POST' }),

  rejectProposal: (projectId, reason) =>
    apiFetch(`/projects/${projectId}/reject?reason=${encodeURIComponent(reason)}`, { method: 'POST' }),

  reportProject: (projectId) => apiFetch(`/projects/${projectId}/report`, { method: 'POST' }),

  // ── Project queries / filters ─────────────────────────────────────────────

  getProjectsByStatus: (status) => apiFetch(`/projects/status?status=${status}`),

  getProjectsByDateRange: (start, end) => apiFetch(`/projects/date-range?start=${start}&end=${end}`),

  filterProjects: ({ neighbourhood, street, type, priority, status, startDate, endDate } = {}) => {
  const params = new URLSearchParams();
  if (neighbourhood) params.append('neighbourhood', neighbourhood);
  if (street) params.append('street', street);
  if (type) params.append('type', type);
  if (priority) params.append('priority', priority);
  if (status) params.append('status', status);
  if (startDate) params.append('startDate', startDate);
  if (endDate) params.append('endDate', endDate);
  return apiFetch(`/projects/filter?${params.toString()}`);
},

  // ── Resident subscriptions ────────────────────────────────────────────────

  subscribeResident: (data) =>
    apiFetch('/subscriptions/residents', { method: 'POST', body: JSON.stringify(data) }),

  getResidentSubscriptions: (residentId) => apiFetch(`/subscriptions/residents?residentId=${residentId}`),

  unsubscribeResident: (id) => apiFetch(`/subscriptions/residents/${id}`, { method: 'DELETE' }),

  reactivateResidentSubscription: (id) =>
    apiFetch(`/subscriptions/residents/${id}/reactivate`, { method: 'PUT' }),

  // ── Provider subscriptions ────────────────────────────────────────────────

  subscribeProvider: (data) =>
    apiFetch('/subscriptions/providers', { method: 'POST', body: JSON.stringify(data) }),

  getProviderSubscriptions: (companyNumber) =>
    apiFetch(`/subscriptions/providers?companyNumber=${encodeURIComponent(companyNumber)}`),

  unsubscribeProvider: (id) => apiFetch(`/subscriptions/providers/${id}`, { method: 'DELETE' }),

  reactivateProviderSubscription: (id) =>
    apiFetch(`/subscriptions/providers/${id}/reactivate`, { method: 'PUT' }),

  // ── Notifications ───────────────────────────────────────────────────────────

  getNotifications: (userId, userType) =>
    apiFetch(`/notifications?userId=${encodeURIComponent(userId)}&userType=${encodeURIComponent(userType)}`),

  getUnreadNotifications: (userId, userType) =>
    apiFetch(`/notifications/unread?userId=${encodeURIComponent(userId)}&userType=${encodeURIComponent(userType)}`),

  markNotificationRead: (notificationId) =>
    apiFetch(`/notifications/${notificationId}/read`, { method: 'PUT' }),
};
import { useEffect, useState } from "react";
import { api } from "../../api/api";
import { useAuth } from "../../auth/AuthContext";
import AppLayout from "../../layouts/AppLayout";
import { useNavigate } from "react-router-dom";
import ProviderProjects from "../../components/ProviderProjects";

export default function MyWorks() {
  return (
    <AppLayout>
    <div>
      <h1>Mes projets</h1>
      <ProviderProjects />
    </div>
    </AppLayout>
  );
}
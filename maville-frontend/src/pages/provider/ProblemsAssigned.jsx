import { useEffect, useState } from 'react';
import { Problems } from '../../components/Problems';
import AppLayout from '../../layouts/AppLayout';

const ProblemsAssigned = () => {
  return (   // ← parentheses required
    <AppLayout>
      <div>
        <h1>Problems assigned</h1>
        <Problems />
      </div>
    </AppLayout>
  );
};

export default ProblemsAssigned;   // ← default export
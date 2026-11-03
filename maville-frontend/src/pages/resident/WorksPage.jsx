import React from 'react';
import {Works} from '../../components/Works';
import AppLayout from '../../layouts/AppLayout';

export default function WorksPage() {
  return (
    <AppLayout>
      <div>
        <Works />
      </div>
    </AppLayout>
  );
}
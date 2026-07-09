import React from 'react';
import {Works} from '../../components/Works';
import AppLayout from '../../layouts/AppLayout';

export default function WorksPage() {
  return (
    <AppLayout>
      <div>
        <h1>Works Filter</h1>
        <Works />
      </div>
    </AppLayout>
  );
}
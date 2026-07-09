// src/pages/provider/CreateProposalPage.jsx
import { useSearchParams } from 'react-router-dom';
import Proposal from '../../components/Proposal';  // adjust path if needed

export default function CreateProposalPage() {
  const [searchParams] = useSearchParams();
  const problemId = searchParams.get('problemId');
  return <Proposal mode="submit" initialProblemId={problemId} />;
}
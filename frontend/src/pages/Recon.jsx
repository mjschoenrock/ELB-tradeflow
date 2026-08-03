/**
 * ============================================================================
 * Recon.jsx — TICKET-I107 / TICKET-I124C
 * ============================================================================
 * WHAT:    Recon-breaks page with modal-based resolution.
 * WHY:     Where Ops users actually resolve breaks safely without blocking event loop.
 * ============================================================================
 */
 import { useState } from 'react';
 import StatusBadge from '../components/StatusBadge.jsx';
 import { useReconResults } from '../hooks/useReconResults.js';
 import ResolveBreakModal from '../components/ResolveBreakModal.jsx';
 
 export default function Recon() {
     const [filter, setFilter] = useState('OPEN');
     const { results, loading, error, refetch } = useReconResults(filter);
 
     // Optimistic state shadow so we can roll back on error.
     const [optimistic, setOptimistic] = useState({});
     
     // Modal state
     const [selectedBreak, setSelectedBreak] = useState(null);
     const [isModalOpen, setIsModalOpen] = useState(false);
 
     const openResolveModal = (breakItem) => {
         setSelectedBreak(breakItem);
         setIsModalOpen(true);
     };
 
     const handleOptimisticResolve = (id) => {
         setOptimistic(prev => ({ ...prev, [id]: 'RESOLVED' }));
     };
 
     const handleRollback = (id) => {
         setOptimistic(prev => {
             const next = { ...prev };
             delete next[id];
             return next;
         });
     };
 
     const visibleResults = results.filter(r => {
         const effectiveStatus = optimistic[r.id] || r.status;
         return filter === 'ALL' ? true : effectiveStatus === filter;
     });
 
     return (
         <>
             <h1>Reconciliation Breaks</h1>
 
             <div className="filters">
                 {['ALL', 'OPEN', 'RESOLVED'].map(s => (
                     <button key={s}
                             className={filter === s ? 'active' : ''}
                             onClick={() => setFilter(s)}>
                         {s}
                     </button>
                 ))}
             </div>
 
             {loading && <div className="loading">Loading…</div>}
             {error && <div className="error">{error.message}</div>}
 
             <table className="data-table">
                 <thead>
                     <tr>
                         <th>Trade Ref</th>
                         <th>Discrepancy</th>
                         <th>Status</th>
                         <th>Action</th>
                     </tr>
                 </thead>
                 <tbody>
                     {visibleResults.map(r => {
                         const status = optimistic[r.id] || r.status;
                         return (
                             <tr key={r.id}>
                                 <td>{r.tradeRef || r.tradeId}</td>
                                 <td>{r.discrepancyType || '—'}</td>
                                 <td><StatusBadge status={status} /></td>
                                 <td>
                                     {status === 'OPEN' && (
                                         <button onClick={() => openResolveModal(r)}>Resolve</button>
                                     )}
                                 </td>
                             </tr>
                         );
                     })}
                 </tbody>
             </table>
 
             <ResolveBreakModal
                 breakItem={selectedBreak}
                 isOpen={isModalOpen}
                 onClose={() => {
                     setIsModalOpen(false);
                     setSelectedBreak(null);
                 }}
                 onResolved={handleOptimisticResolve}
                 onError={handleRollback}
             />
         </>
     );
 }
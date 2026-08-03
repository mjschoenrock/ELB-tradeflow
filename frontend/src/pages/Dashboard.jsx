/**
 * ============================================================================
 * Dashboard.jsx — TICKET-I103
 * ============================================================================
 * WHAT:    Landing page with 4 summary cards.
 * WHY:     The number Ops users look at first thing in the morning.
 * ============================================================================
 *
 *  TODO(TICKET-I103):
 *    - useTradeData + useReconResults
 *    - derive: total trades, matched %, unmatched count, avg processing time
 *    - refresh every 30s (useEffect + setInterval, cleared on unmount)
 * ============================================================================
 */
import { useEffect, useMemo } from 'react';
import StatCard from '../components/StatCard.jsx';
import { useTradeData } from '../hooks/useTradeData.js';
import { useReconResults } from '../hooks/useReconResults.js';

const INTERVAL = 30_000;

export default function Dashboard() {
    const filters = useMemo(() => ({ size: 500 }), []);
    const { trades, loading, refetch: refetchTrades } = useTradeData(filters);
    const { results: openBreaks, refetch: refetchBreaks } = useReconResults('OPEN');
    const { results: resolvedBreaks } = useReconResults('RESOLVED');

    useEffect(() => {
        const id = setInterval(() => {
            refetchTrades();
            refetchBreaks();
        }, INTERVAL);
        return () => clearInterval(id);
    }, [refetchTrades, refetchBreaks]);

    const total = trades.length;
    const matched = trades.filter(t => t.status === 'MATCHED').length;
    const matchedPct = total ? Math.round((matched / total) * 100) + '%' : '—';
    const avgTime = getAvgTime(resolvedBreaks);

    return (
        <>
            <h1>Operations Dashboard</h1>
            <section className="cards">
                <StatCard caption="Total Trades"        value={loading ? '…' : total} />
                <StatCard caption="Matched %"           value={loading ? '…' : matchedPct} />
                <StatCard caption="Unmatched Count"     value={openBreaks.length} />
                <StatCard caption="Avg Processing Time" value={avgTime} />
            </section>
        </>
    );
}

function getAvgTime(resolved) {
    if (!resolved || resolved.length === 0) return 'N/A';

    const check = resolved.filter(a => a.detectedAt && a.resolvedAt);
    if (check.length === 0) return 'N/A';
    
    const totalTime = check.reduce((acc, a) => {
        const ms = new Date(a.resolvedAt) - new Date(a.detectedAt);
        return acc + ms / 3_600_000;
    }, 0);
    return (totalTime / check.length).toFixed(1) + 'h';
}

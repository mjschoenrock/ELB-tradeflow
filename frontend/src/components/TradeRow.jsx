/**
 * ============================================================================
 * TradeRow.jsx — TICKET-I105
 * ============================================================================
 * WHAT:    Expandable row — clicking shows detail panel with counterparty
 *          LEI, instrument ISIN, audit log.
 * ============================================================================
 *  TODO(TICKET-I105):
 *    - useState toggle for expanded / collapsed
 *    - on expand: fetch /api/v1/trades/{id} (or use already-loaded data)
 *    - show metadata in a sub-table or definition list
 *
 *  HINT: KEEP this component small. If it grows past 120 lines, split.
 * ============================================================================
 */
import StatusBadge from './StatusBadge.jsx';

export default function TradeRow({
    trade,
    colSpan = 7,
    expanded = false,
    onToggle
}) {

    const detectedAt = formatDateTime(trade?.createdAt);

    return (
        <>
            <tr>
                <td>
                    <button
                        type="button"
                        className="row-toggle"
                        onClick={onToggle}
                        aria-expanded={expanded}
                        aria-label={`${expanded ? 'Collapse' : 'Expand'} trade ${trade.tradeRef}`}
                    >
                        {expanded ? '▾' : '▸'}
                    </button>{' '}
                    {trade.tradeRef}
                </td>
                <td>{trade.instrumentId}</td>
                <td>{trade.counterpartyId}</td>
                <td>{trade.quantity}</td>
                <td>{trade.price}</td>
                <td>{trade.tradeDate}</td>
                <td><StatusBadge status={trade.status} /></td>
            </tr>
            {expanded && (
                <tr className="trade-row-detail">
                    <td colSpan={colSpan}>
                        <dl className="trade-meta-grid">
                            <dt>Trade ID</dt>
                            <dd>{trade.id ?? '—'}</dd>
                            <dt>Trade Ref</dt>
                            <dd>{trade.tradeRef ?? '—'}</dd>
                            <dt>Instrument ID</dt>
                            <dd>{trade.instrumentId ?? '—'}</dd>
                            <dt>Counterparty ID</dt>
                            <dd>{trade.counterpartyId ?? '—'}</dd>
                            <dt>Status</dt>
                            <dd>{trade.status ?? '—'}</dd>
                            <dt>Created At</dt>
                            <dd>{detectedAt}</dd>
                        </dl>
                    </td>
                </tr>
            )}
        </>
    );
}

function formatDateTime(value) {
    if (!value) return '—';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return String(value);
    return date.toLocaleString('en-GB');
}

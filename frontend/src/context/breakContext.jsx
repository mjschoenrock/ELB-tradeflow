/**
 * ============================================================================
 * BreakContext — TICKET-I124A (Day 9)
 * ============================================================================
 * WHAT:    Shared open-breaks count with a useReducer-backed store, exposed
 *          via a <BreakProvider> wrapper and a useBreaks() custom hook.
 * HOW:     React Context + useReducer. Actions: HYDRATE (initial fetch or
 *          SSE push), RESOLVE (optimistic decrement), REOPEN (rollback).
 * WHY:     The navbar badge, the dashboard StatCard, and the resolve modal
 *          all read the same open-count. When Day-9's Kafka-driven audit
 *          consumer fires, the SSE/WebSocket bridge dispatches HYDRATE and
 *          every subscriber updates at once with no refetch.
 * OBSERVE: React DevTools -> <BreakProvider> at the top of the tree with
 *          state={openCount: N}; click Resolve in the modal and the badge
 *          decrements immediately in both the navbar and the StatCard, with
 *          no network request in the Network tab.
 * ============================================================================
 */
import { createContext, useContext, useEffect, useReducer } from 'react';

const initial = { openCount: 0, lastUpdated: 0 };

function reducer(state, action) {
    switch (action.type) {
        case 'HYDRATE':
            return { openCount: action.count, lastUpdated: Date.now() };
        case 'RESOLVE':
            return { openCount: Math.max(0, state.openCount - 1), lastUpdated: Date.now() };
        case 'REOPEN':
            return { openCount: state.openCount + 1, lastUpdated: Date.now() };
        default:
            return state;
    }
}

const BreakContext = createContext(null);

export function BreakProvider({ children }) {
    const [state, dispatch] = useReducer(reducer, initial);

    // Hydrate from REST on mount. Later SSE/WebSocket pushes re-dispatch
    // HYDRATE with the fresh count when I119's audit consumer fires.
    useEffect(() => {
        fetch('/api/v1/recon/breaks/open-count')
            .then((r) => r.json())
            .then(({ count }) => dispatch({ type: 'HYDRATE', count }))
            .catch(() => {
                /* swallow — badge stays at 0 until the next successful poll */
            });
    }, []);

    return (
        <BreakContext.Provider value={{ state, dispatch }}>
            {children}
        </BreakContext.Provider>
    );
}

export function useBreaks() {
    const ctx = useContext(BreakContext);
    if (!ctx) throw new Error('useBreaks must be used inside <BreakProvider>');
    return ctx;
}
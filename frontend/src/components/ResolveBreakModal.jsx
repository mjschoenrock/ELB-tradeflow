import { useState, useEffect, useRef } from 'react';
import { resolveBreak } from '../services/apiService.js';

export default function ResolveBreakModal({ breakItem, isOpen, onClose, onResolved, onError }) {
    const dialogRef = useRef(null);
    const [note, setNote] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [errorMsg, setErrorMsg] = useState(null);

    useEffect(() => {
        const dialog = dialogRef.current;
        if (!dialog) return;

        if (isOpen) {
            if (!dialog.open) {
                dialog.showModal();
            }
        } else {
            if (dialog.open) {
                dialog.close();
            }
        }
    }, [isOpen]);

    // Handle backdrop click and Esc key via native dialog event
    useEffect(() => {
        const dialog = dialogRef.current;
        if (!dialog) return;

        const handleCancel = (e) => {
            e.preventDefault();
            onClose();
        };

        const handleClick = (e) => {
            const rect = dialog.getBoundingClientRect();
            const isInDialog =
                rect.top <= e.clientY &&
                e.clientY <= rect.bottom &&
                rect.left <= e.clientX &&
                e.clientX <= rect.right;
            if (!isInDialog) {
                onClose();
            }
        };

        dialog.addEventListener('cancel', handleCancel);
        dialog.addEventListener('click', handleClick);
        return () => {
            dialog.removeEventListener('cancel', handleCancel);
            dialog.removeEventListener('click', handleClick);
        };
    }, [onClose]);

    const handleConfirm = async () => {
        if (note.trim().length < 5) {
            setErrorMsg('Resolution note must be at least 5 characters.');
            return;
        }

        setSubmitting(true);
        setErrorMsg(null);

        // Optimistic UI dispatch/update
        onResolved(breakItem.id);

        try {
            await resolveBreak(breakItem.id, { note });
            setSubmitting(false);
            setNote('');
            onClose();
        } catch (e) {
            setSubmitting(false);
            setErrorMsg('Resolve failed: ' + e.message);
            // Rollback optimistic update
            onError(breakItem.id);
        }
    };

    if (!breakItem) return null;

    return (
        <dialog ref={dialogRef} className="resolve-break-modal">
            <div className="modal-content">
                <h3>Resolve Reconciliation Break</h3>
                <div className="break-details">
                    <p><strong>Trade Ref:</strong> {breakItem.tradeRef || breakItem.tradeId}</p>
                    <p><strong>Discrepancy:</strong> {breakItem.discrepancyType || '—'}</p>
                </div>

                <div className="form-group">
                    <label htmlFor="resolution-note">Resolution Note (min 5 chars):</label>
                    <textarea
                        id="resolution-note"
                        autoFocus
                        value={note}
                        onChange={(e) => setNote(e.target.value)}
                        placeholder="Enter details about how this break was resolved..."
                        rows={4}
                    />
                </div>

                {errorMsg && <div className="error-toast" style={{ color: 'red', margin: '8px 0' }}>{errorMsg}</div>}

                <div className="modal-actions">
                    <button type="button" onClick={onClose} disabled={submitting}>
                        Cancel
                    </button>
                    <button
                        type="button"
                        onClick={handleConfirm}
                        disabled={submitting || note.trim().length < 5}
                    >
                        {submitting ? 'Resolving...' : 'Confirm Resolution'}
                    </button>
                </div>
            </div>
        </dialog>
    );
}
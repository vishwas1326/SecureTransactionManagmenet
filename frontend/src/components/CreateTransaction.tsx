import { useState } from "react";
import api from "../services/api";
import "../styles/CreateTransaction.css";

interface CreateTransactionProps {
    onTransactionCreated: () => void;
    onClose: () => void;
}

function CreateTransaction({
    onTransactionCreated,
    onClose
}: CreateTransactionProps) {

    const [form, setForm] = useState({
        transactionType: "PAYMENT",
        amount: "",
        description: "",
        status: "PENDING",
        createdBy: localStorage.getItem("username") || ""
    });

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [submitting, setSubmitting] = useState(false);

    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>
    ) => {

        event.preventDefault();

        try {

            setSubmitting(true);
            setSuccess("");
            setError("");

            const requestBody = {
                transactionType: form.transactionType,
                amount: Number(form.amount),
                description: form.description,
                status: form.status,
                createdBy: form.createdBy
            };

            await api.post(
                "/transactions",
                requestBody
            );

            setSuccess("Transaction created successfully!");
            onTransactionCreated();
            setTimeout(() => {
                onClose();
            }, 2000);

        } catch (error: any) {

            console.error(
                "Unable to create transaction",
                error
            );
            setSuccess("");

            setError(
                error.response?.data?.message ||
                "Unable to create transaction"
            );

        } finally {

            setSubmitting(false);
        }
    };


    return (

        <div className="transaction-modal-overlay">

            <div className="transaction-modal">

                <div className="transaction-modal-header">

                    <h2>New Transaction</h2>

                    <button
                        type="button"
                        onClick={onClose}
                        className="close-button"
                    >
                        ✕
                    </button>

                </div>


                <form onSubmit={handleSubmit}>

                    <div className="form-group">

                        <label>
                            Transaction Type
                        </label>

                        <select
                            value={form.transactionType}
                            onChange={(event) =>
                                setForm({
                                    ...form,
                                    transactionType:
                                        event.target.value
                                })
                            }
                        >
                            <option value="PAYMENT">
                                PAYMENT
                            </option>

                            <option value="TRANSFER">
                                TRANSFER
                            </option>
                            <option value="DEPOSIT">
                                DEPOSIT
                            </option>

                            <option value="WITHDRAWAL">
                                WITHDRAWAL
                            </option>
                        </select>

                    </div>


                    <div className="form-group">

                        <label>
                            Amount
                        </label>

                        <input
                            type="number"
                            step="0.01"
                            min="0.01"
                            value={form.amount}
                            onChange={(event) =>
                                setForm({
                                    ...form,
                                    amount: event.target.value
                                })
                            }
                            required
                        />

                    </div>


                    <div className="form-group">

                        <label>
                            Description
                        </label>

                        <textarea
                            maxLength={250}
                            value={form.description}
                            onChange={(event) =>
                                setForm({
                                    ...form,
                                    description:
                                        event.target.value
                                })
                            }
                        />

                    </div>


                    <div className="form-group">

                        <label>
                            Status
                        </label>

                        <select
                            value={form.status}
                            onChange={(event) =>
                                setForm({
                                    ...form,
                                    status:
                                        event.target.value
                                })
                            }
                        >

                            <option value="PENDING">
                                Pending
                            </option>

                            <option value="COMPLETED">
                                Completed
                            </option>

                            <option value="FAILED">
                                Failed
                            </option>

                            <option value="CANCELLED">
                                Cancelled
                            </option>

                        </select>

                    </div>


                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}
                    {success && (
                        <div className="success-message">
                            {success}
                        </div>
                    )}


                    <div className="form-actions">

                        <button
                            type="button"
                            onClick={onClose}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            disabled={submitting}
                        >

                            {submitting
                                ? "Creating..."
                                : "Create Transaction"}

                        </button>

                    </div>

                </form>

            </div>

        </div>
    );
}

export default CreateTransaction;
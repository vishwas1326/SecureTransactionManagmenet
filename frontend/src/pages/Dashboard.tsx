import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/Dashboard.css";
import TransactionRow from "../components/TransactionRow";
import CreateTransaction from "../components/CreateTransaction";

export interface Transaction {
    id: number;
    transactionReference: string;
    transactionType: string;
    amount: number;
    description: string;
    transactionStatus: string;
    createdBy: string;
    createdAt: string;
    updatedAt: string;
}

function Dashboard() {

    const [transactions, setTransactions] =
        useState<Transaction[]>([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [showCreateTransaction, setShowCreateTransaction] =
        useState(false);

    // Pagination
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);

    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const navigate = useNavigate();

    useEffect(() => {
        loadTransactions();
    }, [page, pageSize]);

    const loadTransactions = async () => {

        try {

            setLoading(true);
            setError("");

            const response = await api.get("/transactions", {
                params: {
                    page: page,
                    size: pageSize
                }
            });

            /*
                Expected Spring Boot response:

                {
                    content: [...],
                    totalElements: 35,
                    totalPages: 4,
                    size: 10,
                    number: 0
                }
            */

            if (response.data.content) {

                setTransactions(response.data.content);

                setTotalPages(response.data.totalPages);
                setTotalElements(response.data.totalElements);

            } else {

                // fallback if backend returns normal array
                setTransactions(response.data);

                setTotalElements(response.data.length);
                setTotalPages(1);
            }

        } catch (error) {

            console.error(error);

            setError("Unable to load transactions");

        } finally {

            setLoading(false);
        }
    };

    const logout = () => {

        localStorage.removeItem("token");

        navigate("/login");
    };

    const goToPage = (newPage: number) => {

        if (
            newPage >= 0 &&
            newPage < totalPages &&
            newPage !== page
        ) {
            setPage(newPage);
        }
    };

    const handlePageSizeChange = (
        event: React.ChangeEvent<HTMLSelectElement>
    ) => {

        setPageSize(Number(event.target.value));

        // Return to first page
        setPage(0);
    };

    const handleTransactionCreated = () => {

        /*
            After creating a transaction,
            return to first page so newest record
            can be displayed.
        */

        if (page === 0) {
            loadTransactions();
        } else {
            setPage(0);
        }

        setShowCreateTransaction(false);
    };

    const getPageNumbers = () => {

        const pages: number[] = [];

        const maxVisiblePages = 5;

        let startPage =
            Math.max(0, page - Math.floor(maxVisiblePages / 2));

        let endPage =
            Math.min(
                totalPages - 1,
                startPage + maxVisiblePages - 1
            );

        if (endPage - startPage + 1 < maxVisiblePages) {

            startPage =
                Math.max(
                    0,
                    endPage - maxVisiblePages + 1
                );
        }

        for (
            let pageNumber = startPage;
            pageNumber <= endPage;
            pageNumber++
        ) {
            pages.push(pageNumber);
        }

        return pages;
    };

    return (

        <div className="dashboard">

            <header className="navbar">

                <div>
                    <h2>Secure Transactions</h2>
                </div>

                <div className="navbar-actions">

                    <span className="user-label">
                        Secure Session
                    </span>

                    <button
                        className="logout-button"
                        onClick={logout}
                    >
                        Logout
                    </button>

                </div>

            </header>

            <main className="dashboard-content">

                <div className="dashboard-heading">

                    <div>

                        <h1>Transaction Dashboard</h1>

                        <p>
                            Monitor and manage your transactions
                        </p>

                    </div>

                    <button
                        className="new-transaction-button"
                        onClick={() =>
                            setShowCreateTransaction(true)
                        }
                    >
                        + New Transaction
                    </button>

                </div>

                {/* Summary Cards */}

                <div className="summary-grid">

                    <div className="summary-card">

                        <span>Total Transactions</span>

                        <strong>
                            {totalElements}
                        </strong>

                    </div>

                    <div className="summary-card">

                        <span>Successful</span>

                        <strong>
                            {
                                transactions.filter(
                                    transaction =>
                                        transaction.transactionStatus ===
                                        "COMPLETED"
                                ).length
                            }
                        </strong>

                    </div>

                    <div className="summary-card">

                        <span>Pending</span>

                        <strong>
                            {
                                transactions.filter(
                                    transaction =>
                                        transaction.transactionStatus ===
                                        "PENDING"
                                ).length
                            }
                        </strong>

                    </div>

                    <div className="summary-card">

                        <span>Failed</span>

                        <strong>
                            {
                                transactions.filter(
                                    transaction =>
                                        transaction.transactionStatus ===
                                        "FAILED"
                                ).length
                            }
                        </strong>

                    </div>

                    <div className="summary-card">

                        <span>Cancelled</span>

                        <strong>
                            {
                                transactions.filter(
                                    transaction =>
                                        transaction.transactionStatus ===
                                        "CANCELLED"
                                ).length
                            }
                        </strong>

                    </div>

                </div>

                {/* Transaction Section */}

                <div className="transaction-section">

                    <div className="transaction-section-header">

                        <div>

                            <h2>Recent Transactions</h2>

                            <p>
                                View all transaction activity
                            </p>

                        </div>

                        {/* Page size */}

                        <div className="page-size-container">

                            <label>
                                Rows per page:
                            </label>

                            <select
                                value={pageSize}
                                onChange={handlePageSizeChange}
                            >
                                <option value={5}>5</option>
                                <option value={10}>10</option>
                                <option value={20}>20</option>
                                <option value={50}>50</option>
                            </select>

                        </div>

                    </div>

                    {loading && (

                        <div className="message">
                            Loading transactions...
                        </div>

                    )}

                    {error && (

                        <div className="error-message">
                            {error}
                        </div>

                    )}

                    {!loading && !error && (

                        <>
                            <div className="table-container">

                                <table>

                                    <thead>

                                        <tr>

                                            <th>Reference</th>

                                            <th>Type</th>

                                            <th>Description</th>

                                            <th>Amount</th>

                                            <th>Status</th>

                                            <th>Created By</th>

                                            <th>Created</th>

                                        </tr>

                                    </thead>

                                    <tbody>

                                        {transactions.length === 0 ? (

                                            <tr>

                                                <td
                                                    colSpan={7}
                                                    className="empty-table"
                                                >
                                                    No transactions found
                                                </td>

                                            </tr>

                                        ) : (

                                            transactions.map(
                                                transaction => (

                                                    <TransactionRow
                                                        key={transaction.id}
                                                        transaction={
                                                            transaction
                                                        }
                                                    />

                                                )
                                            )

                                        )}

                                    </tbody>

                                </table>

                            </div>

                            {/* Pagination */}

                            {totalPages > 0 && (

                                <div className="pagination-container">

                                    <div className="pagination-info">

                                        Page {page + 1} of{" "}
                                        {totalPages}

                                        {" • "}

                                        {totalElements} transactions

                                    </div>

                                    <div className="pagination-controls">

                                        <button
                                            onClick={() =>
                                                goToPage(0)
                                            }
                                            disabled={page === 0}
                                        >
                                            First
                                        </button>

                                        <button
                                            onClick={() =>
                                                goToPage(page - 1)
                                            }
                                            disabled={page === 0}
                                        >
                                            Previous
                                        </button>

                                        {getPageNumbers().map(
                                            pageNumber => (

                                                <button
                                                    key={pageNumber}
                                                    className={
                                                        page === pageNumber
                                                            ? "active-page"
                                                            : ""
                                                    }
                                                    onClick={() =>
                                                        goToPage(
                                                            pageNumber
                                                        )
                                                    }
                                                >
                                                    {pageNumber + 1}
                                                </button>

                                            )
                                        )}

                                        <button
                                            onClick={() =>
                                                goToPage(page + 1)
                                            }
                                            disabled={
                                                page >=
                                                totalPages - 1
                                            }
                                        >
                                            Next
                                        </button>

                                        <button
                                            onClick={() =>
                                                goToPage(
                                                    totalPages - 1
                                                )
                                            }
                                            disabled={
                                                page >=
                                                totalPages - 1
                                            }
                                        >
                                            Last
                                        </button>

                                    </div>

                                </div>

                            )}

                        </>

                    )}

                </div>

            </main>

            {showCreateTransaction && (

                <CreateTransaction
                    onClose={() =>
                        setShowCreateTransaction(false)
                    }
                    onTransactionCreated={
                        handleTransactionCreated
                    }
                />

            )}

        </div>
    );
}

export default Dashboard;
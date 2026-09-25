import type { Transaction } from "../pages/Dashboard";

type TransactionRowProps = {
    transaction: Transaction;
};

function TransactionRow({ transaction }: TransactionRowProps) {

    const formatAmount = (amount: number) => {
        return new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: "USD"
        }).format(amount);
    };

    const formatDate = (date: string) => {
        if (!date) return "-";

        return new Date(date).toLocaleString();
    };

    return (
        <tr>

            <td className="reference">
                {transaction.transactionReference}
            </td>

            <td>
                <span
                    className={`type-badge ${transaction.transactionType.toLowerCase()}`}
                >
                    {transaction.transactionType}
                </span>
            </td>

            <td>
                {transaction.description}
            </td>

            <td className="amount">
                {formatAmount(transaction.amount)}
            </td>

            <td>
                <span
                    className={`status-badge ${transaction.transactionStatus.toLowerCase()}`}
                >
                    {transaction.transactionStatus}
                </span>
            </td>

            <td>
                {transaction.createdBy}
            </td>

            <td>
                {formatDate(transaction.createdAt)}
            </td>

        </tr>
    );
}

export default TransactionRow;
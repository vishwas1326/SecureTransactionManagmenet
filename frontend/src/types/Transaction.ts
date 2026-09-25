export interface Transaction {
  id: number;
  transactionReference: string;
  transactionType: string;
  amount: number;
  description: string;
  status: string;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
}
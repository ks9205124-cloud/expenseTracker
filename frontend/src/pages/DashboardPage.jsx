import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

function DashboardPage() {
    // --- Category States ---
    const [categories, setCategories] = useState([]);
    const [categoriesLoading, setCategoriesLoading] = useState(true);
    const [isAddingCategory, setIsAddingCategory] = useState(false);
    const [newCategoryName, setNewCategoryName] = useState('');
    const [selectedCategoryId, setSelectedCategoryId] = useState(null);

    // --- Expense States ---
    const [expenses, setExpenses] = useState([]);
    const [expensesLoading, setExpensesLoading] = useState(true);
    const [isAddingExpense, setIsAddingExpense] = useState(false);

    // Form inputs for new expense
    const [expenseAmount, setExpenseAmount] = useState('');
    const [expenseDate, setExpenseDate] = useState(new Date().toISOString().split('T')[0]);
    const [expenseCategoryId, setExpenseCategoryId] = useState('');

    // --- DIRECT SUBMISSION LOCKS (Prevents double clicks instantly) ---
    const isSavingExpenseRef = useRef(false);
    const isSavingCategoryRef = useRef(false);

    const token = sessionStorage.getItem('access_token');

    // -------------------------------------------------------------
    // USERNAME EXTRACTION FROM JWT
    // -------------------------------------------------------------
    const getUsernameFromToken = () => {
        if (!token) return 'User';
        try {
            const payloadBase64 = token.split('.')[1];
            const decodedJson = atob(payloadBase64);
            const decodedPayload = JSON.parse(decodedJson);
            return decodedPayload.sub || decodedPayload.username || decodedPayload.email || 'User';
        } catch (err) {
            console.error("Failed to decode token payload:", err);
            return 'User';
        }
    };

    const username = getUsernameFromToken();

    const authHeaders = {
        'Authorization': `Bearer ${token}`
    };

    // -------------------------------------------------------------
    // AUTH / LOGOUT
    // -------------------------------------------------------------
    const handleLogout = () => {
        sessionStorage.clear();
        localStorage.clear();
        window.location.href = '/logout';
    };

    // -------------------------------------------------------------
    // API CALLS: CATEGORIES
    // -------------------------------------------------------------
    const fetchCategories = async () => {
        try {
            const res = await api.get("/api/categories", { headers: authHeaders });
            setCategories(res.data);
        } catch (err) {
            console.error("Error fetching categories:", err);
        } finally {
            setCategoriesLoading(false);
        }
    };

    const handleAddCategory = async () => {
        if (isSavingCategoryRef.current) return; // Immediate lock check
        if (!newCategoryName.trim()) return;

        isSavingCategoryRef.current = true;
        try {
            await api.post("/api/categories", { categoryName: newCategoryName }, { headers: authHeaders });
            setNewCategoryName('');
            setIsAddingCategory(false);
            fetchCategories();
        } finally {
            isSavingCategoryRef.current = false;
        }
    };

    const handleDeleteCategory = async (id, e) => {
        e.stopPropagation();
        await api.delete(`/api/categories/${id}`, { headers: authHeaders });
        fetchCategories();
        fetchExpenses();
        if (selectedCategoryId === id) setSelectedCategoryId(null);
    };

    // -------------------------------------------------------------
    // API CALLS: EXPENSES
    // -------------------------------------------------------------
    const fetchExpenses = async () => {
        try {
            const res = await api.get("/api/expenses", { headers: authHeaders });
            setExpenses(res.data);
        } catch (err) {
            console.error("Error fetching expenses:", err);
        } finally {
            setExpensesLoading(false);
        }
    };

    const handleAddExpense = async () => {
        if (isSavingExpenseRef.current) return; // Immediate lock check
        if (!expenseAmount || !expenseDate || !expenseCategoryId) return;

        isSavingExpenseRef.current = true;
        try {
            await api.post("/api/expenses", {
                amount: parseFloat(expenseAmount),
                date: expenseDate,
                categoryId: Number(expenseCategoryId)
            }, { headers: authHeaders });

            setExpenseAmount('');
            setExpenseCategoryId('');
            setIsAddingExpense(false);
            fetchExpenses();
        } finally {
            isSavingExpenseRef.current = false;
        }
    };

    const handleDeleteExpense = async (id) => {
        await api.delete(`/api/expenses/${id}`, { headers: authHeaders });
        fetchExpenses();
    };

    useEffect(() => {
        fetchCategories();
        fetchExpenses();
    }, []);

    const filteredExpenses = selectedCategoryId
        ? expenses.filter(exp => (exp.category?.id || exp.categoryId) === selectedCategoryId)
        : expenses;

    const totalSpent = filteredExpenses.reduce((sum, exp) => sum + (exp.amount || 0), 0);

    return (
        <div className="min-h-screen bg-slate-50 p-8 text-zinc-900 max-w-6xl mx-auto space-y-6">

            <header className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold capitalize">{username}</h1>
                    <p className="text-sm text-slate-500">Track and manage your expenses effortlessly</p>
                </div>
                <div className="flex items-center gap-3">
                    <span className="text-xs bg-slate-200 px-3 py-1.5 rounded-full text-slate-700 font-medium">
                        Categories: {categories.length}
                    </span>
                    <span className="text-xs bg-teal-100 text-teal-800 px-3 py-1.5 rounded-full font-semibold">
                        Total Spent: ₹{totalSpent.toFixed(2)}
                    </span>
                    <button
                        type="button"
                        onClick={handleLogout}
                        className="ml-2 px-3.5 py-1.5 bg-red-600 hover:bg-red-700 text-white text-xs font-semibold rounded-lg shadow-sm transition-colors cursor-pointer"
                    >
                        Logout
                    </button>
                </div>
            </header>

            {/* 1. CATEGORIES FLEXBOX SECTION */}
            <section className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-xl font-semibold text-slate-800">Categories</h2>
                    {selectedCategoryId && (
                        <button
                            onClick={() => setSelectedCategoryId(null)}
                            className="text-xs text-teal-700 hover:underline font-medium cursor-pointer"
                        >
                            Clear Filter
                        </button>
                    )}
                </div>

                <div className="flex flex-wrap items-center gap-3">
                    {categoriesLoading ? (
                        <p className="text-sm text-slate-500">Loading categories...</p>
                    ) : (
                        categories.map((cat, index) => {
                            const categoryId = cat.id || cat.categoryId;
                            const categoryName = cat.name || cat.categoryName || "Unnamed";
                            const isSelected = selectedCategoryId === categoryId;

                            return (
                                <div
                                    key={`cat-${index}-${categoryId}`}
                                    onClick={() => setSelectedCategoryId(isSelected ? null : categoryId)}
                                    className={`flex items-center gap-2 px-4 py-2 rounded-full border text-sm font-medium transition-all cursor-pointer ${
                                        isSelected
                                            ? 'bg-teal-700 text-white border-teal-700 shadow-md scale-105'
                                            : 'bg-slate-100 text-slate-800 border-slate-300 hover:bg-slate-200'
                                    }`}
                                >
                                    <span>{categoryName}</span>

                                    <button
                                        type="button"
                                        onClick={(e) => handleDeleteCategory(categoryId, e)}
                                        className={`ml-1 text-xs rounded-full p-1 hover:bg-red-500 hover:text-white transition-colors border-0 bg-transparent cursor-pointer ${
                                            isSelected ? 'text-slate-200' : 'text-slate-500'
                                        }`}
                                        title="Delete category"
                                    >
                                        ✕
                                    </button>
                                </div>
                            );
                        })
                    )}

                    {isAddingCategory ? (
                        <div className="flex items-center gap-2">
                            <input
                                type="text"
                                autoFocus
                                placeholder="Category name..."
                                value={newCategoryName}
                                onChange={(e) => setNewCategoryName(e.target.value)}
                                className="px-3 py-1.5 text-sm border border-slate-300 rounded-full focus:outline-none focus:ring-2 focus:ring-teal-700 bg-white"
                            />
                            <button
                                type="button"
                                onClick={handleAddCategory}
                                className="px-3 py-1.5 text-sm bg-teal-800 text-white rounded-full font-medium hover:bg-teal-700 border-0 cursor-pointer"
                            >
                                Save
                            </button>
                            <button
                                type="button"
                                onClick={() => { setIsAddingCategory(false); setNewCategoryName(''); }}
                                className="px-3 py-1.5 text-sm bg-slate-200 text-slate-700 rounded-full font-medium hover:bg-slate-300 cursor-pointer border-0"
                            >
                                Cancel
                            </button>
                        </div>
                    ) : (
                        <button
                            type="button"
                            onClick={() => setIsAddingCategory(true)}
                            className="flex items-center gap-1.5 px-4 py-2 rounded-full border-2 border-dashed border-teal-700 text-teal-800 hover:bg-teal-50 font-medium text-sm transition-colors cursor-pointer bg-white"
                        >
                            <span>+</span>
                            <span>Add Category</span>
                        </button>
                    )}
                </div>
            </section>

            {/* 2. EXPENSES SECTION */}
            <section className="bg-white p-6 rounded-xl shadow-sm border border-slate-200 space-y-4">
                <div className="flex justify-between items-center">
                    <div>
                        <h2 className="text-xl font-semibold text-slate-800">Expenses</h2>
                        {selectedCategoryId && (
                            <p className="text-xs text-teal-700 font-medium">
                                Filtered by selected category
                            </p>
                        )}
                    </div>

                    <button
                        type="button"
                        onClick={() => setIsAddingExpense(!isAddingExpense)}
                        className="px-4 py-2 bg-teal-700 text-white text-sm font-medium rounded-lg hover:bg-teal-800 transition-colors cursor-pointer border-0"
                    >
                        {isAddingExpense ? 'Close Form' : '+ Add Expense'}
                    </button>
                </div>

                {isAddingExpense && (
                    <div className="p-4 bg-slate-50 border border-slate-200 rounded-lg flex flex-wrap gap-4 items-end">
                        <div className="flex-1 min-w-[150px]">
                            <label className="block text-xs font-semibold text-slate-600 mb-1">Amount</label>
                            <input
                                type="number"
                                step="0.01"
                                required
                                placeholder="0.00"
                                value={expenseAmount}
                                onChange={(e) => setExpenseAmount(e.target.value)}
                                className="w-full px-3 py-2 text-sm border border-slate-300 rounded-md focus:ring-2 focus:ring-teal-700 focus:outline-none bg-white"
                            />
                        </div>

                        <div className="flex-1 min-w-[150px]">
                            <label className="block text-xs font-semibold text-slate-600 mb-1">Date</label>
                            <input
                                type="date"
                                required
                                value={expenseDate}
                                onChange={(e) => setExpenseDate(e.target.value)}
                                className="w-full px-3 py-2 text-sm border border-slate-300 rounded-md focus:ring-2 focus:ring-teal-700 focus:outline-none bg-white"
                            />
                        </div>

                        <div className="flex-1 min-w-[150px]">
                            <label className="block text-xs font-semibold text-slate-600 mb-1">Category</label>
                            <select
                                required
                                value={expenseCategoryId}
                                onChange={(e) => setExpenseCategoryId(e.target.value)}
                                className="w-full px-3 py-2 text-sm border border-slate-300 rounded-md focus:ring-2 focus:ring-teal-700 focus:outline-none bg-white"
                            >
                                <option value="">Select Category</option>
                                {categories.map((cat) => {
                                    const id = cat.id || cat.categoryId;
                                    const name = cat.name || cat.categoryName;
                                    return (
                                        <option key={id} value={id}>
                                            {name}
                                        </option>
                                    );
                                })}
                            </select>
                        </div>

                        <button
                            type="button"
                            onClick={handleAddExpense}
                            className="px-5 py-2 bg-teal-800 text-white font-medium text-sm rounded-md hover:bg-teal-700 transition-colors border-0 cursor-pointer"
                        >
                            Save Expense
                        </button>
                    </div>
                )}

                <div className="space-y-2">
                    {expensesLoading ? (
                        <p className="text-sm text-slate-500 py-4">Loading expenses...</p>
                    ) : filteredExpenses.length === 0 ? (
                        <div className="text-center py-8 text-slate-400 border-2 border-dashed border-slate-100 rounded-lg">
                            No expenses found.
                        </div>
                    ) : (
                        filteredExpenses.map((exp) => {
                            const categoryName = exp.category?.name || exp.categoryName || "General";

                            return (
                                <div
                                    key={exp.id}
                                    className="flex items-center justify-between p-4 bg-white border border-slate-200 rounded-xl hover:border-slate-300 hover:shadow-sm transition-all"
                                >
                                    <div className="flex items-center gap-4">
                                        <span className="px-3 py-1 bg-slate-100 text-slate-700 text-xs font-medium rounded-full border border-slate-200">
                                            {categoryName}
                                        </span>
                                        <span className="text-sm text-slate-500 font-mono">
                                            {exp.date}
                                        </span>
                                    </div>

                                    <div className="flex items-center gap-4">
                                        <span className="text-base font-semibold text-slate-900">
                                            ₹{parseFloat(exp.amount).toFixed(2)}
                                        </span>

                                        <button
                                            type="button"
                                            onClick={() => handleDeleteExpense(exp.id)}
                                            className="text-slate-400 hover:text-red-500 p-1.5 rounded-lg hover:bg-red-50 transition-colors border-0 bg-transparent cursor-pointer"
                                            title="Delete expense"
                                        >
                                            ✕
                                        </button>
                                    </div>
                                </div>
                            );
                        })
                    )}
                </div>
            </section>
        </div>
    );
}

export default DashboardPage;
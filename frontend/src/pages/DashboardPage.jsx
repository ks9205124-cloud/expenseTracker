function DashboardPage() {
    const token = sessionStorage.getItem('access_token');

    return (
        <div className="min-h-screen flex flex-col items-center justify-center gap-4">
            <h1 className="text-3xl font-bold">Dashboard (placeholder)</h1>
            <p className="text-sm text-zinc-600 break-all max-w-md text-center">
                Token: {token ? token.substring(0, 40) + '...' : 'No token found'}
            </p>
        </div>
    );
}

export default DashboardPage;
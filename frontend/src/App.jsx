import { useState } from 'react'
import loginBg from './assets/login-bg.jpg'

function App() {
    return (
        <>
            <div
                className="min-h-screen bg-cover bg-center"
                style={{ backgroundImage: `url(${loginBg})` }}
            >
            </div>
        </>
    )
}

export default App
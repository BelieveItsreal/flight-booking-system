import { Outlet } from "react-router";
import Navbar from "../Navbar/Navbar.jsx";

function Layout() {
    return(
        <div className="min-h-screen bg-surface">
            <Navbar />
            <main>
                <Outlet />
            </main>
        </div>
    )
}

export default Layout

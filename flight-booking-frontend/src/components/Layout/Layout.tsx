import { Outlet } from "react-router";
import Navbar from "../Navbar/Navbar.tsx";

function Layout() {
    return(
        <div className="min-h-screen bg-surface">
            <a
                href="#main"
                className="sr-only focus:not-sr-only focus:fixed focus:top-3 focus:left-3 focus:z-60 focus:rounded-lg
                focus:bg-primary focus:px-4 focus:py-2 focus:text-sm focus:font-medium focus:text-white"
            >
                Skip to the main content
            </a>
            <Navbar />
            {/* id is the skip link's target; tabIndex lets it take focus without showing an outline */}
            <main id="main" tabIndex={-1} className="outline-none">
                <Outlet />
            </main>
        </div>
    )
}

export default Layout

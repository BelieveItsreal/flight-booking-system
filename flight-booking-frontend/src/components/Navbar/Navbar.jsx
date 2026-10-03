import {Link, NavLink} from 'react-router'
import airplane from "../../assets/illustrations/airplane.svg";
import UserMenu from "./UserMenu.jsx";

const NAV_LINKS = [
    {to: '/', label: 'Flights'},
    {to: '/my-bookings', label: 'My Bookings'},
    {to: '/offers', label: 'Offers'},
    {to: '/support', label: 'Support'},
]

function Navbar() {
    return (
        <header className="sticky top-0 z-50 h-16 border-b border-line bg-surface">
            <div className="wrapper flex h-full items-center justify-between">
                <Link to="/" className="flex items-center gap-2 text-primary">
                    <img src={airplane} className="size-10" alt="airplane svg" />
                    <span className="text-2xl font-bold tracking-tight">SkyBook</span>
                </Link>
                <nav className="flex h-full items-center gap-10">
                    {NAV_LINKS.map((link) => (
                        <NavLink
                            key={link.to}
                            to={link.to}
                            end={link.to === '/'}
                            className={({ isActive }) =>
                                `relative flex h-full items-center text-[15px] font-medium transition-colors
                                after:absolute after:inset-x-0 after:bottom-0 after:h-0.75 after:rounded-t after:bg-primary after:transition-transform
                                ${isActive
                                    ? 'text-primary after:scale-x-100'
                                    : 'text-ink hover:text-primary after:scale-x-0'}`
                            }
                        >
                            {link.label}
                        </NavLink>
                    ))}
                </nav>
                {/* Right: avatar + dropdown */}
                <UserMenu />
            </div>
        </header>
    )
}

export default Navbar

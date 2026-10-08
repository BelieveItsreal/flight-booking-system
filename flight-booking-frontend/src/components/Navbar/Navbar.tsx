import { useRef, useState, type KeyboardEvent } from 'react'
import { Link, NavLink, type NavLinkRenderProps } from 'react-router'
import { Menu, X } from 'lucide-react'
import airplane from "../../assets/illustrations/airplane.svg";
import UserMenu from "./UserMenu.tsx";
import { useClickOutside } from '../../hooks/useClickOutside.ts'

const NAV_LINKS = [
    {to: '/', label: 'Flights'},
    {to: '/my-bookings', label: 'My Bookings'},
    {to: '/offers', label: 'Offers'},
    {to: '/support', label: 'Support'},
]
const desktopLinkClass = ({isActive}: NavLinkRenderProps) =>
    `relative flex h-full items-center text-[15px] font-medium transition-colors
    after:absolute after:inset-x-0 after:bottom-0 after:h-0.75 after:rounded-t after:bg-primary after:transition-transform
    ${isActive
        ? 'text-primary after:scale-x-100'
        : 'text-ink hover:text-primary after:scale-x-0 hover:after:scale-x-100'}`
const mobileLinkClass = ({ isActive }: NavLinkRenderProps) =>
    `block rounded-lg px-3 py-3 text-base font-medium transition-colors
    ${isActive ? 'bg-subtle text-primary' : 'text-ink hover:bg-subtle hover:text-primary'}`
function Navbar() {
    const[isMenuOpen, setIsMenuOpen] = useState(false)
    const headerRef = useRef<HTMLElement>(null)
    const toggleRef = useRef<HTMLButtonElement>(null)
    const closeMenu = () => setIsMenuOpen(false)
    useClickOutside(headerRef, isMenuOpen, closeMenu)
    const handleKeyDown = (event: KeyboardEvent<HTMLElement>) => {
        if (event.key === 'Escape' && isMenuOpen){
            closeMenu()
            toggleRef.current?.focus()
        }
    }
    return (
        <header
            ref={headerRef}
            onKeyDown={handleKeyDown}
            className="sticky top-0 z-50 h-16 border-b border-line bg-surface"
        >
            <div className="wrapper flex h-full items-center justify-between gap-4">
                <Link to="/" onClick={closeMenu} className="flex items-center gap-2 rounded-md text-primary">
                    <img src={airplane} className="size-8 sm:size-10" alt="airplane svg" />
                    <span className="text-2xl font-bold tracking-tight sm:text-2xl">SkyBook</span>
                </Link>
                <nav aria-label="Main" className="hidden h-full items-center gap-6 md:flex lg:gap-10">
                    {NAV_LINKS.map((link) => (
                        <NavLink key={link.to} to={link.to} end={link.to === '/'} className={desktopLinkClass}>
                            {link.label}
                        </NavLink>
                    ))}
                </nav>
                {/* Right: avatar + dropdown */}
                <div className="flex items-center gap-1 sm:gap-2">
                    <UserMenu />
                    <button
                        ref={toggleRef}
                        type="button"
                        onClick={() => setIsMenuOpen((open) => !open)}
                        aria-expanded={isMenuOpen}
                        aria-controls="mobile-menu"
                        aria-label={isMenuOpen ? 'Close menu' : 'Open menu'}
                        className="grid size-10 place-items-center rounded-full text-ink transition-colors hover:bg-subtle
                        hover:text-primary active:scale-95 md:hidden"
                    >
                        {isMenuOpen ? <X className="size-6" /> : <Menu className="size-6" />}
                    </button>
                </div>
            </div>
            <div
                id="mobile-menu"
                inert={!isMenuOpen}
                className={`absolute inset-x-0 top-full grid bg-surface transition-[grid-template-rows,opacity,box-shadow]
                duration-300 ease-out md:hidden ${
                    isMenuOpen
                        ? 'grid-rows-[1fr] border-b border-line opacity-100 shadow-float'
                        : 'grid-rows-[0fr] opacity-0'
                }`}
            >
                <nav aria-label="Main mobile" className="overflow-hidden">
                    <ul className="wrapper flex flex-col gap-1 py-3">
                        {NAV_LINKS.map((link) => (
                            <li key={link.to}>
                                <NavLink
                                    to={link.to}
                                    end={link.to === '/'}
                                    onClick={closeMenu}
                                    className={mobileLinkClass}
                                >
                                    {link.label}
                                </NavLink>
                            </li>
                        ))}
                    </ul>
                </nav>
            </div>
        </header>
    )
}

export default Navbar

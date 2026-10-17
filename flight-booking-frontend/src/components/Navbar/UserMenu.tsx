import { useRef, useState } from 'react'
import { Link } from 'react-router'
import { ChevronDown, LogOut, Ticket, UserRound } from 'lucide-react'
import { avatar } from '../../assets'
import { useClickOutside } from '../../hooks/useClickOutside.ts'

const user = { name: 'John Doe', avatar }
const itemClass =
    'flex w-full items-center gap-3 px-4 py-2 text-sm text-ink transition-colors hover:bg-subtle hover:text-primary'
function UserMenu(){
    const [isOpen, setIsOpen] = useState(false)
    const menuRef = useRef<HTMLDivElement>(null)

    const closeMenu = () => setIsOpen(false)
    useClickOutside(menuRef, isOpen, closeMenu)

    const handleLogout = () => {
        closeMenu()
        console.log('Logout Clicked')
    }
    // A plain disclosure (button + list of links), not role="menu": that role promises arrow-key navigation
    return(
        <div ref={menuRef} className="relative">
            <button
                type="button"
                onClick={() => setIsOpen((open) => !open)}
                aria-expanded={isOpen}
                aria-controls="user-menu"
                aria-label={`Account menu for ${user.name}`}
                className="flex items-center gap-2 rounded-full py-1 pr-2 pl-1 transition-colors hover:bg-subtle sm:gap-3 sm:pr-3"
            >
                <img src={user.avatar} alt="" className="size-9 rounded-full object-cover" />
                {/* Name hidden on phones so avatar + hamburger fit on a 320px screen */}
                <span className="hidden text-[15px] font-medium text-ink sm:inline">{user.name}</span>
                <ChevronDown
                    className={`size-4 text-muted transition-transform ${isOpen ? 'rotate-180' : ''}`}
                    />
            </button>
            {isOpen &&(
                <div
                    id="user-menu"
                    className="absolute top-full right-0 z-50 mt-2 w-48 rounded-xl border border-line bg-surface py-2 shadow-float"
                >
                    <Link to="/profile" onClick={closeMenu} className={itemClass}>
                        <UserRound className="size-4"/>
                        Profile
                    </Link>
                    <Link to="/my-bookings" onClick={closeMenu} className={itemClass}>
                        <Ticket className="size-4" />
                        My Bookings
                    </Link>
                    <hr className="my-2 border-line" />
                    <button type="button" onClick={handleLogout} className={`${itemClass} text-accent`}>
                        <LogOut className="size-4" />
                        Logout
                    </button>
                </div>
            )}
        </div>
    )
}

export default UserMenu

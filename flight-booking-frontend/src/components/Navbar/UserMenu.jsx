import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router'
import { ChevronDown, LogOut, Ticket, UserRound } from 'lucide-react'
import { avatar } from '../../assets'

const user = { name: 'John Doe', avatar }
const itemClass =
    'flex w-full items-center gap-3 px-4 py-2 text-sm text-ink transition-colors hover:bg-subtle hover:text-primary'
function UserMenu(){
    const [isOpen, setIsOpen] = useState(false)
    const menuRef = useRef(null)

    useEffect(() => {
        if (!isOpen) return
        function handleClickOutside(event){
            if (menuRef.current && !menuRef.current.contains(event.target)){
                setIsOpen(false)
            }
        }
        function handleEscape(event){
            if (event.key === 'Escape') setIsOpen(false)
        }
        document.addEventListener('mousedown', handleClickOutside)
        document.addEventListener('keydown', handleEscape)
        return() => {
            document.removeEventListener('mousedown', handleClickOutside)
            document.removeEventListener('keydown', handleEscape)
        }
    }, [isOpen])

    const closeMenu = () => setIsOpen(false)
    const handleLogout = () => {
        closeMenu()
        console.log('Logout Clicked')
    }
    return(
        <div ref={menuRef} className="relative">
            <button
                type="button"
                onClick={() => setIsOpen((open) => !open)}
                aria-haspopup="menu"
                aria-expanded={isOpen}
                className="flex items-center gap-3 rounded-full py-1 pr-3 pl-1 transition-colors hover:bg-subtle"
            >
                <img src={user.avatar} alt="" className="size-9 rounded-full object-cover" />
                <span className="text-[15px] font-medium text-ink">{user.name}</span>
                <ChevronDown
                    className={`size-4 text-muted transition-transform ${isOpen ? 'rotate-180' : ''}`}
                    />
            </button>
            {isOpen &&(
                <div
                    role="menu"
                    className="absolute top-full right-0 z-50 mt-2 w-48 rounded-xl border border-line bg-surface py-2 shadow-float"
                >
                    <Link to="/profile" role="menuitem" onClick={closeMenu} className={itemClass}>
                        <UserRound className="size-4"/>
                        Profile
                    </Link>
                    <Link to="/my-bookings" role="menuitem" onClick={closeMenu} className={itemClass}>
                        <Ticket className="size-4" />
                        My Bookings
                    </Link>
                    <hr className="my-2 border-line" />
                    <button type="button" role="menuitem" onClick={handleLogout} className={`${itemClass} text-accent`}>
                        <LogOut className="size-4" />
                        Logout
                    </button>
                </div>
            )}
        </div>
    )
}

export default UserMenu
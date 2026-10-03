import { useEffect } from 'react'

// Calls onClose when the user clicks outside `ref` or presses Escape
export function useClickOutside(ref, isOpen, onClose) {
    useEffect(() => {
        if (!isOpen) return

        function handleClickOutside(event) {
            if (ref.current && !ref.current.contains(event.target)) onClose()
        }

        function handleEscape(event) {
            if (event.key === 'Escape') onClose()
        }

        document.addEventListener('mousedown', handleClickOutside)
        document.addEventListener('keydown', handleEscape)

        return () => {
            document.removeEventListener('mousedown', handleClickOutside)
            document.removeEventListener('keydown', handleEscape)
        }
    }, [ref, isOpen, onClose])
}

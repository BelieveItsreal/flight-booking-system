import { useEffect, type RefObject } from 'react'

// Calls onClose when the user clicks outside `ref` or presses Escape
export function useClickOutside(ref: RefObject<HTMLElement | null>, isOpen: boolean, onClose: () => void) {
    useEffect(() => {
        if (!isOpen) return

        function handleClickOutside(event: MouseEvent) {
            if (ref.current && !ref.current.contains(event.target as Node)) onClose()
        }

        function handleEscape(event: KeyboardEvent) {
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

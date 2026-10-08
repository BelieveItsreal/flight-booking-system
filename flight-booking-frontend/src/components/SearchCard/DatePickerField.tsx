import { useRef, useState } from 'react'
import { DayPicker } from 'react-day-picker'
import { format } from 'date-fns'
import { CalendarDays } from 'lucide-react'
import SearchField from './SearchField.tsx'
import { useClickOutside } from '../../hooks/useClickOutside.ts'

interface DatePickerFieldProps {
    label: string
    selected?: Date
    onSelect: (date: Date) => void
    placeholder?: string
    disabled?: boolean
    minDate?: Date
    className?: string
}

function DatePickerField({
                             label,
                             selected,
                             onSelect,
                             placeholder = 'Select date',
                             disabled = false,
                             minDate = new Date(),
                             className = '',
                         }: DatePickerFieldProps) {
    const [isOpen, setIsOpen] = useState(false)
    const wrapperRef = useRef<HTMLDivElement>(null)
    useClickOutside(wrapperRef, isOpen, () => setIsOpen(false))
    const handleSelect = (date: Date | undefined) => {
        if (!date) return
        onSelect(date)
        setIsOpen(false)
    }
    return (
        <div ref={wrapperRef} className="relative">
            <SearchField
                icon={CalendarDays}
                label={label}
                value={selected ? format(selected, 'd MMM, EEE') : ''}
                placeholder={placeholder}
                disabled={disabled}
                onClick={() => setIsOpen((open) => !open)}
                className={className}
            />
            {isOpen && (
                <div className="absolute top-full left-0 z-30 mt-2 rounded-xl border border-line bg-surface p-3 shadow-float">
                    <DayPicker
                        mode="single"
                        selected={selected}
                        onSelect={handleSelect}
                        defaultMonth={selected ?? minDate}
                        disabled={{ before: minDate }}
                    />
                </div>
            )}
        </div>
    )
}

export default DatePickerField

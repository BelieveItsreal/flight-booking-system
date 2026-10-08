import { useRef, useState } from 'react'
import { Minus, Plus, User } from 'lucide-react'
import SearchField from './SearchField.tsx'
import { useClickOutside } from '../../hooks/useClickOutside.ts'
import type { CabinClass, Travellers } from '../../store/searchSlice.ts'

const CABIN_CLASSES: CabinClass[] = ['Economy', 'Business']
const MAX_TRAVELLERS = 9

// Builds the field text, e.g. "1 Traveller, Economy" or "3 Travellers, Business"
function getLabel({ adults, children, infants, cabinClass }: Travellers) {
    const total = adults + children + infants
    return `${total} Traveller${total > 1 ? 's' : ''}, ${cabinClass}`
}

interface CounterProps {
    label: string
    hint: string
    value: number
    onChange: (value: number) => void
    min: number
    max: number
}

function Counter({ label, hint, value, onChange, min, max }: CounterProps) {
    const buttonClass =
        'grid size-8 place-items-center rounded-full border border-line text-primary transition-colors hover:bg-subtle disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-transparent'

    return (
        <div className="flex items-center justify-between py-2">
            <div>
                <p className="text-sm font-medium text-ink">{label}</p>
                <p className="text-xs text-muted">{hint}</p>
            </div>
            <div className="flex items-center gap-3">
                <button
                    type="button"
                    aria-label={`Remove ${label}`}
                    onClick={() => onChange(value - 1)}
                    disabled={value <= min}
                    className={buttonClass}
                >
                    <Minus className="size-4" />
                </button>
                <span className="w-4 text-center text-sm font-semibold">{value}</span>
                <button
                    type="button"
                    aria-label={`Add ${label}`}
                    onClick={() => onChange(value + 1)}
                    disabled={value >= max}
                    className={buttonClass}
                >
                    <Plus className="size-4" />
                </button>
            </div>
        </div>
    )
}

interface TravellerClassFieldProps {
    value: Travellers
    onChange: (value: Travellers) => void
    className?: string
}

function TravellerClassField({ value, onChange, className = '' }: TravellerClassFieldProps) {
    const [isOpen, setIsOpen] = useState(false)
    const wrapperRef = useRef<HTMLDivElement>(null)

    useClickOutside(wrapperRef, isOpen, () => setIsOpen(false))

    const { adults, children, infants, cabinClass } = value
    const seatsLeft = MAX_TRAVELLERS - adults - children

    // Update one field and keep the rest
    const update = (changes: Partial<Travellers>) => onChange({ ...value, ...changes })

    const handleAdultsChange = (newAdults: number) => {
        // Each infant sits on an adult's lap, so infants can't exceed adults
        update({ adults: newAdults, infants: Math.min(infants, newAdults) })
    }

    return (
        <div ref={wrapperRef} className="relative">
            <SearchField
                icon={User}
                label="Traveller & Class"
                value={getLabel(value)}
                onClick={() => setIsOpen((open) => !open)}
                className={className}
            />

            {isOpen && (
                <div className="absolute top-full right-0 z-30 mt-2 w-80 rounded-xl border border-line bg-surface p-4 shadow-float">
                    <p className="mb-1 text-xs font-semibold tracking-wide text-muted uppercase">Travellers</p>
                    <div className="divide-y divide-line">
                        <Counter
                            label="Adults"
                            hint="12+ years"
                            value={adults}
                            onChange={handleAdultsChange}
                            min={1}
                            max={adults + seatsLeft}
                        />
                        <Counter
                            label="Children"
                            hint="2–12 years"
                            value={children}
                            onChange={(n) => update({ children: n })}
                            min={0}
                            max={children + seatsLeft}
                        />
                        <Counter
                            label="Infants"
                            hint="Under 2 years, on lap"
                            value={infants}
                            onChange={(n) => update({ infants: n })}
                            min={0}
                            max={adults}
                        />
                    </div>

                    <p className="mt-4 mb-2 text-xs font-semibold tracking-wide text-muted uppercase">Class</p>
                    <div className="grid grid-cols-2 gap-2">
                        {CABIN_CLASSES.map((option) => (
                            <button
                                key={option}
                                type="button"
                                aria-pressed={cabinClass === option}
                                onClick={() => update({ cabinClass: option })}
                                className={`rounded-lg border px-3 py-2 text-sm font-medium transition-colors ${
                                    cabinClass === option
                                        ? 'border-primary bg-primary text-white'
                                        : 'border-line text-ink hover:bg-subtle'
                                }`}
                            >
                                {option}
                            </button>
                        ))}
                    </div>

                    <button
                        type="button"
                        onClick={() => setIsOpen(false)}
                        className="mt-4 w-full rounded-lg bg-accent py-2.5 text-sm font-semibold text-white transition-colors hover:bg-accent-dark"
                    >
                        Done
                    </button>
                </div>
            )}
        </div>
    )
}

export default TravellerClassField

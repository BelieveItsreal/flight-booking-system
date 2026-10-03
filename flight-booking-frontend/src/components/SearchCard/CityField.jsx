import {useEffect, useId, useRef, useState} from "react";
import {useClickOutside} from "../../hooks/useClickOutside.js";
import {formatAirport, searchAirports} from "../../data/airports.js";
import SearchField from "./SearchField.jsx";

function CityField({label, icon: Icon, value, onSelect, error}) {
    const [isOpen, setIsOpen] = useState(false)
    const [query, setQuery] = useState('')
    const [activeIndex, setActiveIndex] = useState(0)
    const wrapperRef = useRef(null)
    const listRef = useRef(null)
    const listId = useId()
    useClickOutside(wrapperRef, isOpen, () => setIsOpen(false))

    // Keep the highlighted city visible when moving with the arrow keys
    useEffect(() => {
        if (!isOpen) return
        listRef.current?.children[activeIndex]?.scrollIntoView({ block: 'nearest' })
    }, [activeIndex, isOpen])
    const results = searchAirports(query)
    const boxClass = `rounded-lg border ${error ? 'border-red-500' : 'border-line'}`
    const open = () => {
        setQuery('')
        setActiveIndex(0)
        setIsOpen(true)
    }
    const choose = (airport) => {
        onSelect(airport)
        setIsOpen(false)
    }
    const handleKeyDown = (event) => {
       if (event.key === 'ArrowDown'){
           event.preventDefault()
           setActiveIndex((i) => Math.min(i+1, results.length-1))
       }else if (event.key === 'ArrowUp'){
           event.preventDefault()
           setActiveIndex((i) => Math.max(i-1, 0))
       }else if (event.key === 'Enter'){
           event.preventDefault()
           if (results[activeIndex]) choose(results[activeIndex])
       }
    }
    return(
        <div ref={wrapperRef} className="relative">
            {isOpen? (
                <div className={`flex items-center gap-3 px-4 py-2.5 ring-1 ring-primary/15 ${boxClass}`}>
                    <Icon className="size-5 shrink-0 text-primary" />
                    <span className="flex min-w-0 flex-1 flex-col">
                        <span className="text-xs text-muted">{label}</span>
                        <input
                            autoFocus
                            role="combobox"
                            aria-expanded="true"
                            aria-controls={listId}
                            aria-autocomplete="list"
                            value={query}
                            onChange={(event) => {
                                setQuery(event.target.value)
                                setActiveIndex(0)
                            }}
                            onKeyDown={handleKeyDown}
                            placeholder="Type city or airport"
                            className="w-full bg-transparent text-[15px] font-medium text-ink outline-none placeholder:font-normal placeholder:text-disabled"
                        />
                    </span>
                </div>
            ):(
                <SearchField
                    icon={Icon}
                    label={label}
                    value={formatAirport(value)}
                    placeholder="Select city"
                    onClick={open}
                    className={boxClass}
                />
            )}
            {isOpen && (
                <ul
                    ref={listRef}
                    id={listId}
                    role="listbox"
                    className="absolute top-full left-0 z-30 mt-2 max-h-72 w-full min-w-72 overflow-y-auto rounded-xl
                    border border-line bg-surface py-2 shadow-float"
                >
                    {results.length === 0 ? (
                        <li className="px-4 py-3 text-sm text-muted">No airports found</li>
                    ):(
                        results.map((airport, index) => (
                            <li
                                key={airport.code}
                                role="option"
                                aria-selected={index === activeIndex}
                                onClick={() => choose(airport)}
                                onMouseEnter={() => setActiveIndex(index)}
                                className={`flex cursor-pointer items-center justify-between gap-3 px-4 py-2 ${
                                    index === activeIndex ? 'bg-subtle' : ''
                                }`}
                            >
                                <span className={"min-w-0"}>
                                    <span className="block text-sm font-medium text-ink">{airport.city}</span>
                                    <span className="block truncate text-xs text-muted">{airport.name}</span>
                                </span>
                                <span className="text-xs font-semibold text-primary">{airport.code}</span>
                            </li>
                        ))
                    )}
                </ul>
            )}
            {error && <p className="mt-1 text-xs text-red-600">{error}</p>}
        </div>
    )
}
export default CityField
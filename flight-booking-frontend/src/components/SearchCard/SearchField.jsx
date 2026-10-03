function SearchField({icon: Icon, label, value, placeholder, disabled = false, className= '', onClick}) {
    return(
        <button
            type="button"
            disabled={disabled}
            onClick={onClick}
            className={`flex w-full items-center gap-3 px-4 py-2.5 text-left transition-colors
                hover:bg-subtle disabled:cursor-not-allowed disabled:bg-subtle ${className}`}
        >
            <Icon className={`size-5 shrink-0 ${disabled? 'text-disabled' : 'text-primary'}`}/>
            <span className="flex min-w-0 flex-col">
                <span className="text-xs text-muted">{label}</span>
                {value ? (
                    <span className="truncate text-[15px] font-medium text-ink">{value}</span>
                ):(
                    <span className="truncate text-[15px] text-disabled">{placeholder}</span>
                )}
            </span>
        </button>
    )
}

export default SearchField
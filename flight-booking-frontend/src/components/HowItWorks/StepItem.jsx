function StepItem({ number, title, description, image }) {
    return(
        <li className="flex items-center gap-4">
            <div className="flex shrink-0 items-center">
            <span aria-hidden="true" className="text-display leading-none font-extrabold tracking-tighter text-primary">
                {number}
            </span>
                <img src={image} alt="" className="-ml-1 size-16" />
            </div>
            <div>
                <h3 className="text-base font-semibold text-ink">
                    {number}. {title}
                </h3>
                <p className="mt-1 max-w-48 text-sm leading-snug text-muted">{description}</p>
            </div>
        </li>
    )
}

export default StepItem
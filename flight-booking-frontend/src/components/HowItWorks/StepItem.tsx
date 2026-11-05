interface StepItemProps {
    number: number
    title: string
    description: string
    image: string
}

function StepItem({ number, title, description, image }: StepItemProps) {
    return(
        // Fixed width when stacked so all three steps line up on the left instead of each being centered
        <li className="flex w-full max-w-xs items-center gap-4 md:w-auto md:max-w-none">
            <div className="flex shrink-0 items-center">
            <span aria-hidden="true" className="text-display leading-none font-extrabold tracking-tighter text-primary">
                {number}
            </span>
                <img src={image} alt="" loading="lazy" decoding="async" className="-ml-1 size-16" />
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

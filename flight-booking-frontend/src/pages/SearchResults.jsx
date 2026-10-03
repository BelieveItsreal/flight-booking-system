import {Link, useSearchParams} from "react-router";

function SearchResults() {
    const [params] = useSearchParams()
    const from = params.get('from')
    const to = params.get('to')
    const depart = params.get('depart')
    const returnDate = params.get('return')
    const adults = params.get('adults')
    const cabinClass = params.get('class')
    return(
        <div className="wrapper py-8">
            <h1 className="text-2xl font-bold text-primary">
                {from} → {to}
            </h1>
            <p className="mt-1 text-muted">
                {depart}
                {returnDate && ` – ${returnDate}`} · {adults} adult(s) · {cabinClass}
            </p>
            <p className="mt-6 text-ink">Flight result will appear here (step 10)</p>
            <Link to="/" className="mt-4 inline-block text-accent">
                ← Modify search
            </Link>
        </div>
    )
}
export default SearchResults
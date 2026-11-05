import type {Airline} from "../../data/airlines.ts";
interface AirlineLogoProps{
    airline: Airline
}
function AirlineLogo({ airline }: AirlineLogoProps){
    if (airline.logo){
        return <img src={airline.logo} alt="" className="size-9 shrink-0 rounded-md object-contain" />
    }
    return (
        <span
            aria-hidden="true"
            className="grid size-9 shrink-0 rounded-md place-items-center text-xs font-bold text-white"
            style={{ backgroundColor: airline.color}}
        >
            {airline.code}
        </span>
    )
}
export default AirlineLogo
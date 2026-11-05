import { logoAirIndia, logoIndiGo, logoSpiceJet, logoVistara } from '../assets'

export type AirlineCode = '6E' | 'AI' | 'UK' | 'SG'
export interface Airline{
    code: AirlineCode
    name: string
    color: string
    logo?: string
}

export const AIRLINES: Record<AirlineCode, Airline> = {
    '6E': { code: '6E', name: 'IndiGo', color: '#09009b', logo: logoIndiGo },
    AI: { code: 'AI', name: 'Air India', color: '#da0b14', logo: logoAirIndia },
    UK: { code: 'UK', name: 'Vistara', color: '#5f2569', logo: logoVistara },
    SG: { code: 'SG', name: 'SpiceJet', color: '#db383e', logo: logoSpiceJet },
}

export const AIRLINE_LIST: Airline[] = [AIRLINES.AI, AIRLINES["6E"], AIRLINES.UK, AIRLINES.SG]

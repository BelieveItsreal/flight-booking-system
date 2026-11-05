import { stepHold, stepSearch, stepTicket } from '../assets'

export interface HowItWorksStep {
    id: string
    title: string
    description: string
    image: string
}

export const HOW_IT_WORKS_STEPS: HowItWorksStep[] = [
    {
        id: 'search',
        title: 'Search Flights',
        description: 'Find the best flights & deals for your journey.',
        image: stepSearch,
    },
    {
        id: 'hold',
        title: 'Hold Your Seat',
        description: 'Lock your fare and pay later with flexibility.',
        image: stepHold,
    },
    {
        id: 'ticket',
        title: 'Instant E-Ticket',
        description: 'Receive your confirmed e-ticket immediately via email.',
        image: stepTicket,
    },
]

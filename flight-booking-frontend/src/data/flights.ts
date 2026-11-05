import type { AirlineCode } from './airlines.ts'
import type { CabinClass } from '../store/searchSlice.ts'
import { addMinutes } from '../utils/flight.ts'

export interface Fare{
    price: number
    seatsLeft: number
}

export interface Flight{
    id: string
    airline: AirlineCode
    flightNumber: string
    from: string
    to: string
    departureTime: string
    arrivalTime: string
    durationMinutes: number
    stops: number
    via?: string
    fares: Record<CabinClass, Fare>
}

export type ScheduledFlight = Omit<Flight, 'id' | 'from' | 'to'>
type FareTuple = [price: number, seatsLeft: number]
// Short builder so each flight fits on one line; the arrival time is calculated, so it always matches the duration
function flight(
    airline: AirlineCode,
    number: number,
    departureTime: string,
    durationMinutes: number,
    economy: FareTuple,
    business: FareTuple,
    via?: string,
): ScheduledFlight {
    return {
        airline,
        flightNumber: `${airline}-${number}`,
        departureTime,
        arrivalTime: addMinutes(departureTime, durationMinutes),
        durationMinutes,
        stops: via ? 1 : 0,
        via,
        fares: {
            Economy: { price: economy[0], seatsLeft: economy[1] },
            Business: { price: business[0], seatsLeft: business[1] },
        },
    }
}
// 40 flights = 8 pages of 5. The first five are the cards in the mockup, and they're the five cheapest,
// so page 1 sorted by "Cheapest" looks exactly like the design. No flight lands after midnight.
export const FLIGHT_SCHEDULE: ScheduledFlight[] = [
    // airline, number, departs, minutes, [economy ₹, seats], [business ₹, seats], via
    flight('6E', 534, '06:15', 130, [4299, 12], [11999, 3]),
    flight('AI', 202, '08:00', 135, [4599, 8], [12499, 5]),
    flight('UK', 995, '09:30', 140, [4799, 15], [13199, 7]),
    flight('SG', 811, '11:00', 145, [5099, 10], [13899, 4]),
    flight('6E', 206, '14:45', 150, [5299, 6], [14499, 2]),
    // Non-stop
    flight('AI', 865, '07:05', 125, [5999, 9], [13499, 6]),   // fastest: 2h 05m
    flight('6E', 2175, '05:30', 135, [5399, 14], [12199, 8]),
    flight('SG', 157, '06:40', 140, [5449, 11], [11899, 5]),
    flight('UK', 943, '07:45', 135, [5899, 7], [13999, 4]),
    flight('6E', 5326, '08:30', 140, [5499, 18], [12399, 9]),
    flight('AI', 887, '09:00', 130, [6199, 5], [14299, 3]),
    flight('SG', 8169, '10:10', 150, [5599, 13], [11799, 6]),
    flight('UK', 927, '10:45', 135, [6299, 6], [14599, 2]),
    flight('6E', 6045, '11:30', 140, [5649, 20], [12699, 7]),
    flight('AI', 805, '12:15', 135, [6399, 10], [13799, 5]),
    flight('6E', 2014, '13:00', 145, [5749, 9], [12899, 4]),
    flight('SG', 9271, '13:40', 150, [5799, 12], [11599, 8]),
    flight('UK', 977, '14:10', 130, [6499, 8], [14799, 3]),
    flight('AI', 659, '15:00', 140, [6599, 4], [13299, 2]),
    flight('6E', 179, '15:35', 135, [5849, 16], [12599, 6]),
    flight('SG', 723, '16:05', 145, [5949, 7], [11999, 5]),
    flight('UK', 955, '16:50', 135, [6799, 11], [14199, 6]),
    flight('6E', 6814, '17:20', 140, [6049, 10], [12999, 3]),
    flight('AI', 624, '17:55', 130, [6899, 6], [13599, 4]),
    flight('SG', 8701, '18:30', 150, [6149, 15], [12299, 9]),
    flight('UK', 993, '19:00', 135, [7199, 5], [14999, 2]),
    flight('6E', 5153, '19:40', 140, [6349, 8], [13399, 5]),
    flight('AI', 687, '20:15', 135, [7399, 9], [14399, 6]),
    flight('SG', 165, '20:45', 145, [6449, 3], [12799, 2]),
    flight('6E', 2096, '21:15', 140, [6699, 12], [13699, 7]),
    flight('UK', 963, '21:40', 130, [7499, 6], [14899, 3]),
    // 1 stop: longer flights with cheaper Business fares
    flight('6E', 7231, '05:50', 255, [5379, 17], [10999, 8], 'JAI'),
    flight('AI', 413, '06:55', 290, [5549, 10], [11499, 6], 'AMD'),
    flight('SG', 2951, '08:15', 310, [5399, 9], [10799, 7], 'HYD'),
    flight('6E', 6421, '09:50', 270, [5699, 13], [11299, 4], 'LKO'),
    flight('UK', 851, '11:20', 300, [6099, 8], [12099, 5], 'HYD'),
    flight('AI', 539, '12:40', 285, [5999, 11], [11699, 6], 'LKO'),
    flight('SG', 489, '14:20', 320, [5499, 6], [10599, 4], 'BLR'),
    flight('6E', 6732, '15:15', 265, [5899, 14], [11899, 8], 'AMD'),
    flight('AI', 471, '16:30', 295, [6249, 7], [12199, 3], 'JAI'),
]


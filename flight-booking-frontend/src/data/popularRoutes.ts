import { destDelhi, destDubai, destGoa } from '../assets'

export interface PopularRoute {
    id: string
    from: string
    to: string
    image: string
    imageAlt: string
    tripType: string
    price: number
}

export const POPULAR_ROUTES: PopularRoute[] = [
    {
        id: 'DEL-BOM',
        from: 'DEL',
        to: 'BOM',
        image: destDelhi,
        imageAlt: 'India Gate in Delhi at sunset',
        tripType: 'One way, Non-stop',
        price: 4299,
    },
    {
        id: 'BLR-GOI',
        from: 'BLR',
        to: 'GOI',
        image: destGoa,
        imageAlt: 'Palm trees on a beach in Goa',
        tripType: 'One way, Non-stop',
        price: 3150,
    },
    {
        id: 'BOM-DXB',
        from: 'BOM',
        to: 'DXB',
        image: destDubai,
        imageAlt: 'Dubai skyline with the Burj Khalifa',
        tripType: 'One way, Non-stop',
        price: 12500,
    },
]

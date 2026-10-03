const inrFormatter = new Intl.NumberFormat('en-IN',{
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
})
export const formatINR = (amount) => inrFormatter.format(amount)
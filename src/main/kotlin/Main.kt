const val LIMIT_PER_DAY: UInt = 15000000U
const val LIMIT_PER_MONTH: UInt = 60000000U

const val MAX_TRANSFER_FROM_VKPAY_ONCE: UInt = 1500000U
const val MAX_TRANSFER_FROM_VKPAY_PER_MONTH: UInt = 4000000U

const val COMMISSION_VISA_MIR: Double = 0.75
const val COMMISSION_VISA_MIR_MIN: UInt = 3500U

const val COMMISSION_MASTERCARD_MAESTRO: Double = 0.6
const val COMMISSION_MASTERCARD_MAESTRO_ADD: UInt = 2000U

const val MASTERCARD_MAESTRO_NO_COMMISSION_MIN_PER_MONTH: UInt = 30000U
const val MASTERCARD_MAESTRO_NO_COMMISSION_MAX_PER_MONTH: UInt = 7500000U

enum class CardTypes {
    MASTERCARD, MAESTRO, VISA, MIR, VKPAY
}

fun main() {
    var mountAmountMasterCard: UInt = 0U
    var mountAmountVkPay: UInt = 0U
    var mountAmountVisa: UInt = 0U
    var amount: UInt
    var cardType: CardTypes

    cardType = CardTypes.MASTERCARD
    print("Введите сумму перевода $cardType в рублях: ")
    amount = readLine()?.toDouble()?.times(100)?.toUInt() ?: return
    mountAmountMasterCard += doTransfer(
        amount = amount,
        monthTransferAmount = mountAmountMasterCard,
        cardType = cardType
    )

    print("Введите сумму перевода $cardType в рублях: ")
    amount = readLine()?.toDouble()?.times(100)?.toUInt() ?: return
    mountAmountMasterCard += doTransfer(
        amount = amount,
        monthTransferAmount = mountAmountMasterCard,
        cardType = cardType
    )

    cardType = CardTypes.VISA
    print("Введите сумму перевода $cardType в рублях: ")
    amount = readLine()?.toDouble()?.times(100)?.toUInt() ?: return
    mountAmountVisa += doTransfer(amount = amount, monthTransferAmount = mountAmountVisa, cardType = cardType)

    print("Введите сумму перевода $cardType в рублях: ")
    amount = readLine()?.toDouble()?.times(100)?.toUInt() ?: return
    mountAmountVisa += doTransfer(amount = amount, monthTransferAmount = mountAmountVisa, cardType = cardType)

    cardType = CardTypes.VKPAY
    print("Введите сумму перевода $cardType в рублях: ")
    amount = readLine()?.toDouble()?.times(100)?.toUInt() ?: return
    mountAmountVkPay += doTransfer(amount = amount, monthTransferAmount = mountAmountVkPay, cardType = cardType)

    print("Введите сумму перевода $cardType в рублях: ")
    amount = readLine()?.toDouble()?.times(100)?.toUInt() ?: return
    mountAmountVkPay += doTransfer(amount = amount, monthTransferAmount = mountAmountVkPay, cardType = cardType)

    print("Введите сумму перевода $cardType в рублях: ")
    amount = readLine()?.toDouble()?.times(100)?.toUInt() ?: return
    mountAmountVkPay += doTransfer(amount = amount, monthTransferAmount = mountAmountVkPay, cardType = cardType)
}

private fun doTransfer(
    amount: UInt,
    monthTransferAmount: UInt,
    cardType: CardTypes = CardTypes.VKPAY
): UInt {
    if (checkLimits(amount, cardType, monthTransferAmount = monthTransferAmount)) {
        val commission =
            calculateCommission(cardType = cardType, monthTransferAmount = monthTransferAmount, amount = amount)
        println("Ваша комиссия составляет ${commission.toDouble() / 100} рублей")
        return amount
    } else {
        println("Вы превысили допустимый лимит")
    }
    return 0U
}

fun calculateCommission(cardType: CardTypes = CardTypes.VKPAY, monthTransferAmount: UInt = 0U, amount: UInt) =
    when (cardType) {
        CardTypes.MASTERCARD, CardTypes.MAESTRO -> {
            if (monthTransferAmount in
                MASTERCARD_MAESTRO_NO_COMMISSION_MIN_PER_MONTH..MASTERCARD_MAESTRO_NO_COMMISSION_MAX_PER_MONTH
            ) {
                0U
            } else {
                (amount.toDouble() * COMMISSION_MASTERCARD_MAESTRO / 100).toUInt() +
                        COMMISSION_MASTERCARD_MAESTRO_ADD
            }
        }
        CardTypes.VISA, CardTypes.MIR -> {
            val commission: UInt = (amount.toDouble() * COMMISSION_VISA_MIR / 100).toUInt()
            if (commission < COMMISSION_VISA_MIR_MIN) COMMISSION_VISA_MIR_MIN else commission
        }
        CardTypes.VKPAY -> 0U
    }

fun checkLimits(
    amount: UInt,
    cardType: CardTypes = CardTypes.VKPAY,
    dailyTransferAmount: UInt = 0U,
    dailyRecieveAmount: UInt = 0U,
    monthTransferAmount: UInt = 0U,
    monthRecieveAmount: UInt = 0U
): Boolean {
    if (cardType == CardTypes.VKPAY) {
        if (amount > MAX_TRANSFER_FROM_VKPAY_ONCE) return false
        if (amount + monthTransferAmount > MAX_TRANSFER_FROM_VKPAY_PER_MONTH) return false
    } else {
        if (amount + dailyTransferAmount > LIMIT_PER_DAY ||
            amount + dailyRecieveAmount > LIMIT_PER_DAY
        ) return false

        if (amount + monthTransferAmount > LIMIT_PER_MONTH ||
            amount + monthRecieveAmount > LIMIT_PER_MONTH
        ) return false
    }

    return true
}
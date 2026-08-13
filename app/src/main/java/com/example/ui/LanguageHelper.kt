package com.example.ui

object Strings {
    fun getText(key: String, lang: String = "EN"): String {
        val isBn = lang == "BN"
        return when (key) {
            "app_title" -> if (isBn) "লটোলিন - লাইভ লটারি" else "LottoWin - Live Lottery"
            "home" -> if (isBn) "হোম" else "Home"
            "tickets" -> if (isBn) "আমার টিকেট" else "My Tickets"
            "results" -> if (isBn) "ফলাফল" else "Results"
            "wallet" -> if (isBn) "ওয়ালেট" else "Wallet"
            "admin" -> if (isBn) "এডমিন প্যানেল" else "Admin Panel"
            "jackpot" -> if (isBn) "জ্যাকপট" else "Jackpot"
            "buy_now" -> if (isBn) "টিকেট কিনুন" else "Buy Ticket"
            "draw_in" -> if (isBn) "ড্র হতে বাকি" else "Draw in"
            "balance" -> if (isBn) "ব্যালেন্স" else "Balance"
            "deposit" -> if (isBn) "ডিপোজিট" else "Deposit"
            "withdraw" -> if (isBn) "উইথড্র" else "Withdraw"
            "quick_pick" -> if (isBn) "অটো পিক (র্যান্ডম)" else "Quick Pick"
            "confirm_purchase" -> if (isBn) "টিকেট কনফার্ম করুন" else "Confirm Purchase"
            "winning_numbers" -> if (isBn) "বিজয়ী নম্বরসমূহ" else "Winning Numbers"
            "live_draw" -> if (isBn) "লাইভ ড্র চলছে" else "Live Draw"
            "total_payout" -> if (isBn) "মোট পেআউট" else "Total Payout"
            "transactions" -> if (isBn) "লেনদেন ইতিহাস" else "Transactions"
            "approve" -> if (isBn) "অনুমোদন করুন" else "Approve"
            "reject" -> if (isBn) "বাতিল করুন" else "Reject"
            "pending_approvals" -> if (isBn) "পেন্ডিং রিকোয়েস্ট" else "Pending Requests"
            "create_draw" -> if (isBn) "নতুন ড্র তৈরি করুন" else "Create New Draw"
            "force_draw" -> if (isBn) "এখনই ড্র পরিচালনা করুন" else "Draw Now"
            else -> key
        }
    }
}

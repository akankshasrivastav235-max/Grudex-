package com.example.model

object NearbyShopsRepository {

    val allLucknowShops: List<NearbyShop> = listOf(
        // 🍬 Mithai Dukaan
        NearbyShop(
            id = "sweet_1",
            nameHindi = "Ram Asrey Sweets",
            category = ShopCategory.SWEETS,
            addressHindi = "Nawal Kishore Road, Hazratganj, Lucknow",
            distanceMeters = 350,
            distanceTextHindi = "350 meter door",
            rating = 4.8f,
            reviewCount = 2850,
            specialtyHindi = "Prasiddh Malai Paan, Motichoor Ladoo aur Gulab Jamun",
            isOpenNow = true,
            mapCoordX = 0.45f,
            mapCoordY = 0.40f
        ),
        NearbyShop(
            id = "sweet_2",
            nameHindi = "Moti Mahal Deluxe Sweets",
            category = ShopCategory.SWEETS,
            addressHindi = "MG Marg, Hazratganj Crossing, Lucknow",
            distanceMeters = 550,
            distanceTextHindi = "550 meter door",
            rating = 4.6f,
            reviewCount = 1640,
            specialtyHindi = "Gond ke Ladoo, Kesar Rasmalai aur Dhokla",
            isOpenNow = true,
            mapCoordX = 0.58f,
            mapCoordY = 0.48f
        ),
        NearbyShop(
            id = "sweet_3",
            nameHindi = "Netram Ajay Kumar Golwale",
            category = ShopCategory.SWEETS,
            addressHindi = "Sri Ram Road, Aminabad, Lucknow",
            distanceMeters = 900,
            distanceTextHindi = "900 meter door",
            rating = 4.7f,
            reviewCount = 3120,
            specialtyHindi = "Shuddh Desi Ghee ki Imarti aur Jalebi",
            isOpenNow = true,
            mapCoordX = 0.35f,
            mapCoordY = 0.58f
        ),
        NearbyShop(
            id = "sweet_4",
            nameHindi = "Chhappan Bhog Sweets",
            category = ShopCategory.SWEETS,
            addressHindi = "Sadar Bazar, Cantonment, Lucknow",
            distanceMeters = 1200,
            distanceTextHindi = "1.2 km door",
            rating = 4.9f,
            reviewCount = 5400,
            specialtyHindi = "Kaju Barfi, Mewa Bites aur Taaza Chena",
            isOpenNow = true,
            mapCoordX = 0.68f,
            mapCoordY = 0.35f
        ),
        NearbyShop(
            id = "sweet_5",
            nameHindi = "Radhey Lal Parampara Sweets",
            category = ShopCategory.SWEETS,
            addressHindi = "Chowk Main Chauraha, Lucknow",
            distanceMeters = 1800,
            distanceTextHindi = "1.8 km door",
            rating = 4.7f,
            reviewCount = 1980,
            specialtyHindi = "Makhan Malai, Rabri Jalebi aur Rasgulla",
            isOpenNow = true,
            mapCoordX = 0.28f,
            mapCoordY = 0.25f
        ),

        // ☕ Chai & Nashta
        NearbyShop(
            id = "tea_1",
            nameHindi = "Sharma Tea Stall (Sharma Ji Ki Chai)",
            category = ShopCategory.TEA_SNACKS,
            addressHindi = "Lalbagh Chauraha, Lucknow",
            distanceMeters = 280,
            distanceTextHindi = "280 meter door",
            rating = 4.9f,
            reviewCount = 7200,
            specialtyHindi = "Kullhad Masala Chai, Gol Samose aur Bun Makkhan",
            isOpenNow = true,
            mapCoordX = 0.48f,
            mapCoordY = 0.38f
        ),
        NearbyShop(
            id = "tea_2",
            nameHindi = "Bajpayee Kachodi Bhandar",
            category = ShopCategory.TEA_SNACKS,
            addressHindi = "Bank Road, Hazratganj, Lucknow",
            distanceMeters = 420,
            distanceTextHindi = "420 meter door",
            rating = 4.8f,
            reviewCount = 4300,
            specialtyHindi = "Crispy Khasta Kachodi, Aloo Chhole aur Adrak Chai",
            isOpenNow = true,
            mapCoordX = 0.54f,
            mapCoordY = 0.44f
        ),
        NearbyShop(
            id = "tea_3",
            nameHindi = "Kewal Zafrani Tea Stall",
            category = ShopCategory.TEA_SNACKS,
            addressHindi = "Janpath Market Gali, Hazratganj, Lucknow",
            distanceMeters = 650,
            distanceTextHindi = "650 meter door",
            rating = 4.7f,
            reviewCount = 2100,
            specialtyHindi = "Zafrani Kesar Chai, Maskabun aur Suji Toast",
            isOpenNow = true,
            mapCoordX = 0.38f,
            mapCoordY = 0.46f
        ),
        NearbyShop(
            id = "tea_4",
            nameHindi = "Chhappan Bhog Chai Counter",
            category = ShopCategory.TEA_SNACKS,
            addressHindi = "Cantonment Road, Sadar, Lucknow",
            distanceMeters = 1100,
            distanceTextHindi = "1.1 km door",
            rating = 4.6f,
            reviewCount = 1450,
            specialtyHindi = "Special Elaichi Chai aur Garma-Garam Poha",
            isOpenNow = true,
            mapCoordX = 0.72f,
            mapCoordY = 0.42f
        ),
        NearbyShop(
            id = "tea_5",
            nameHindi = "Rattilal Ke Khaste & Special Chai",
            category = ShopCategory.TEA_SNACKS,
            addressHindi = "Sadar-Aminabad Link Road, Lucknow",
            distanceMeters = 1400,
            distanceTextHindi = "1.4 km door",
            rating = 4.8f,
            reviewCount = 3900,
            specialtyHindi = "Garma-garam khaste, dahi jalebi aur kadak chai",
            isOpenNow = true,
            mapCoordX = 0.62f,
            mapCoordY = 0.28f
        ),

        // 💊 Medical Store
        NearbyShop(
            id = "med_1",
            nameHindi = "Aminabad Medical Hall (24x7)",
            category = ShopCategory.MEDICAL,
            addressHindi = "Main Chauraha, Aminabad, Lucknow",
            distanceMeters = 350,
            distanceTextHindi = "350 meter door",
            rating = 4.8f,
            reviewCount = 1800,
            specialtyHindi = "24 Ghante Khula • Emergency dawaein aur BP check",
            isOpenNow = true,
            mapCoordX = 0.36f,
            mapCoordY = 0.52f
        ),
        NearbyShop(
            id = "med_2",
            nameHindi = "Sanjivani Chemist & Druggist",
            category = ShopCategory.MEDICAL,
            addressHindi = "MG Marg, Hazratganj, Lucknow",
            distanceMeters = 500,
            distanceTextHindi = "500 meter door",
            rating = 4.7f,
            reviewCount = 1200,
            specialtyHindi = "Sabhi Allopathic dawaein aur Baby Care products",
            isOpenNow = true,
            mapCoordX = 0.52f,
            mapCoordY = 0.40f
        ),
        NearbyShop(
            id = "med_3",
            nameHindi = "Apollo Pharmacy",
            category = ShopCategory.MEDICAL,
            addressHindi = "Jopling Road, Hazratganj, Lucknow",
            distanceMeters = 850,
            distanceTextHindi = "850 meter door",
            rating = 4.6f,
            reviewCount = 950,
            specialtyHindi = "Prescription medicines aur Health supplements",
            isOpenNow = true,
            mapCoordX = 0.65f,
            mapCoordY = 0.50f
        ),
        NearbyShop(
            id = "med_4",
            nameHindi = "Avadh Medical & Surgical Store",
            category = ShopCategory.MEDICAL,
            addressHindi = "Kaiserbagh Bus Stand Road, Lucknow",
            distanceMeters = 1300,
            distanceTextHindi = "1.3 km door",
            rating = 4.5f,
            reviewCount = 680,
            specialtyHindi = "First Aid Kit, Bandages aur Ointments",
            isOpenNow = true,
            mapCoordX = 0.26f,
            mapCoordY = 0.42f
        ),
        NearbyShop(
            id = "med_5",
            nameHindi = "Dr. B.R. Medical Agencies",
            category = ShopCategory.MEDICAL,
            addressHindi = "Station Road, Charbagh, Lucknow",
            distanceMeters = 1900,
            distanceTextHindi = "1.9 km door",
            rating = 4.6f,
            reviewCount = 1100,
            specialtyHindi = "Emergency dawaein aur Ayurvedic upchar",
            isOpenNow = true,
            mapCoordX = 0.42f,
            mapCoordY = 0.68f
        ),

        // 🛒 Kirana Store
        NearbyShop(
            id = "groc_1",
            nameHindi = "Lucknow Kirana & Super Bhandar",
            category = ShopCategory.GROCERY,
            addressHindi = "Janpath Market, Hazratganj, Lucknow",
            distanceMeters = 220,
            distanceTextHindi = "220 meter door",
            rating = 4.7f,
            reviewCount = 1420,
            specialtyHindi = "Taaza Atta, Daal, Masale, Ghee aur Tel",
            isOpenNow = true,
            mapCoordX = 0.46f,
            mapCoordY = 0.52f
        ),
        NearbyShop(
            id = "groc_2",
            nameHindi = "Chawla Provision & Daily Store",
            category = ShopCategory.GROCERY,
            addressHindi = "Lalbagh Main Market, Lucknow",
            distanceMeters = 600,
            distanceTextHindi = "600 meter door",
            rating = 4.8f,
            reviewCount = 2200,
            specialtyHindi = "Dairy products, Bread, Biscuit aur Cold drinks",
            isOpenNow = true,
            mapCoordX = 0.40f,
            mapCoordY = 0.36f
        ),
        NearbyShop(
            id = "groc_3",
            nameHindi = "Verma General & Kirana Store",
            category = ShopCategory.GROCERY,
            addressHindi = "Aminabad Crossing, Lucknow",
            distanceMeters = 950,
            distanceTextHindi = "950 meter door",
            rating = 4.6f,
            reviewCount = 1320,
            specialtyHindi = "Gharelu kirana saman, Masale aur Soaps",
            isOpenNow = true,
            mapCoordX = 0.32f,
            mapCoordY = 0.62f
        ),
        NearbyShop(
            id = "groc_4",
            nameHindi = "Garg Super Mart & Kirana",
            category = ShopCategory.GROCERY,
            addressHindi = "Sadar Bazar Main Market, Lucknow",
            distanceMeters = 1250,
            distanceTextHindi = "1.25 km door",
            rating = 4.6f,
            reviewCount = 1750,
            specialtyHindi = "Ration packing, Atta chakki aur Daily essentials",
            isOpenNow = true,
            mapCoordX = 0.70f,
            mapCoordY = 0.30f
        ),
        NearbyShop(
            id = "groc_5",
            nameHindi = "Awadh Daily Needs & Kirana",
            category = ShopCategory.GROCERY,
            addressHindi = "Near Gole Darwaza, Chowk, Lucknow",
            distanceMeters = 1700,
            distanceTextHindi = "1.7 km door",
            rating = 4.5f,
            reviewCount = 890,
            specialtyHindi = "Desi Masale, Basmati Chawal aur Dry Fruits",
            isOpenNow = true,
            mapCoordX = 0.22f,
            mapCoordY = 0.30f
        )
    )

    fun getShopsByCategory(category: ShopCategory): List<NearbyShop> {
        return allLucknowShops.filter { it.category == category }
    }
}

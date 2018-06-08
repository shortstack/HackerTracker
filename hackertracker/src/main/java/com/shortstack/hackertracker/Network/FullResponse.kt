package com.shortstack.hackertracker.network

import com.shortstack.hackertracker.models.FAQs
import com.shortstack.hackertracker.models.response.Speakers
import com.shortstack.hackertracker.models.response.Types
import com.shortstack.hackertracker.models.response.Vendors

class FullResponse(val syncResponse: SyncResponse, val types: Types, val speakers: Speakers, val vendors: Vendors, val faqs: FAQs)
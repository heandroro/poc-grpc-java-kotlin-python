// Examples: SOLID Principles
// Source: best-practices/SKILL.md

package com.example.app;

// ❌ Anti-pattern: múltiplas responsabilidades
class OrderManagerBad {
    void createOrder() { }
    void sendEmail() { }      // viola SRP
    void generateInvoice() { } // viola SRP
}

// ✅ SOLID: cada serviço com responsabilidade única
class OrderService { 
    void create() { }
}

class NotificationService { 
    void sendEmail() { }
}

class InvoiceService { 
    void generate() { }
}

from typing import List, Dict, Tuple
from collections import namedtuple

# Class to represent a sale record
class SaleRecord:
    def __init__(self, product_id: str, quantity: int, price: float, region: str):
        self._product_id = product_id
        self._quantity = quantity
        self._price = price
        self._region = region

    # Properties to ensure immutability-like behavior
    @property
    def product_id(self) -> str:
        return self._product_id

    @property
    def quantity(self) -> int:
        return self._quantity

    @property
    def price(self) -> float:
        return self._price

    @property
    def region(self) -> str:
        return self._region

    def is_valid(self) -> bool:
        """Check if the sale record is valid."""
        return self.quantity > 0 and self.price > 0

    def calculate_revenue(self) -> float:
        """Calculate revenue for this sale."""
        return self.quantity * self.price

# Class to analyze sales data
class SalesAnalyzer:
    def __init__(self, sales: List[SaleRecord]):
        self._sales = sales

    def get_valid_sales(self) -> List[SaleRecord]:
        """Filter valid sales records."""
        return [sale for sale in self._sales if sale.is_valid()]

    def calculate_total_revenue(self) -> float:
        """Calculate total revenue for valid sales."""
        return sum(sale.calculate_revenue() for sale in self.get_valid_sales())

    def calculate_average_revenue(self) -> float:
        """Calculate average revenue per valid sale."""
        valid_sales = self.get_valid_sales()
        return (
            self.calculate_total_revenue() / len(valid_sales)
            if valid_sales
            else 0.0
        )

    def group_by_product(self) -> Dict[str, int]:
        """Group sales by product and sum quantities."""
        product_totals = {}
        for sale in self.get_valid_sales():
            product_totals[sale.product_id] = (
                product_totals.get(sale.product_id, 0) + sale.quantity
            )
        return product_totals

    def get_top_product(self) -> Tuple[str, int]:
        """Find the top-selling product by total quantity."""
        product_totals = self.group_by_product()
        return max(
            product_totals.items(),
            key=lambda x: x[1],
            default=('None', 0)
        )

    def calculate_revenue_by_region(self, region: str) -> float:
        """Calculate total revenue for a specific region."""
        return sum(
            sale.calculate_revenue()
            for sale in self.get_valid_sales()
            if sale.region == region
        )

    def analyze(self) -> Dict:
        """Perform full sales analysis."""
        total_revenue = self.calculate_total_revenue()
        avg_revenue = self.calculate_average_revenue()
        top_product = self.get_top_product()
        north_revenue = self.calculate_revenue_by_region('North')

        return {
            'total_revenue': round(total_revenue, 2),
            'average_revenue': round(avg_revenue, 2),
            'top_product': {
                'product_id': top_product[0],
                'total_quantity': top_product[1]
            },
            'north_region_revenue': round(north_revenue, 2)
        }

# Sample sales data
sales_data = [
    SaleRecord('P001', 10, 25.0, 'North'),
    SaleRecord('P002', -5, 15.0, 'South'),  # Invalid: negative quantity
    SaleRecord('P001', 20, 25.0, 'East'),
    SaleRecord('P003', 15, 30.0, 'West'),
    SaleRecord('P002', 8, 15.0, 'North'),
    SaleRecord('P004', 0, 50.0, 'South'),   # Invalid: zero quantity
    SaleRecord('P003', 12, 30.0, 'East'),
]

# Run analysis
analyzer = SalesAnalyzer(sales_data)
result = analyzer.analyze()

# Print results
print("Sales Analysis Results:")
print(f"Total Revenue: ${result['total_revenue']}")
print(f"Average Revenue per Sale: ${result['average_revenue']}")
print(f"Top-Selling Product: {result['top_product']['product_id']} "
      f"({result['top_product']['total_quantity']} units)")
print(f"North Region Revenue: ${result['north_region_revenue']}")


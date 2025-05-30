from functools import reduce, partial
from typing import List, Dict, Tuple
from collections import namedtuple

# Define immutable data structure using namedtuple
SaleRecord = namedtuple('SaleRecord', ['product_id', 'quantity', 'price', 'region'])

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

# Pure function to validate a sale record
def is_valid_sale(sale: SaleRecord) -> bool:
    return sale.quantity > 0 and sale.price > 0

# Pure function to calculate revenue for a single sale
def calculate_revenue(sale: SaleRecord) -> float:
    return sale.quantity * sale.price

# Pure function to group sales by product and sum quantities
def group_by_product(acc: Dict[str, int], sale: SaleRecord) -> Dict[str, int]:
    acc[sale.product_id] = acc.get(sale.product_id, 0) + sale.quantity
    return acc

# Higher-order function to filter sales by region
def filter_by_region(region: str) -> callable:
    return lambda sale: sale.region == region

# Main functional pipeline
def analyze_sales(sales: List[SaleRecord]) -> Dict:
    # Filter valid sales
    valid_sales = list(filter(is_valid_sale, sales))
    
    # Calculate total revenue using map and reduce
    revenues = list(map(calculate_revenue, valid_sales))
    total_revenue = reduce(lambda x, y: x + y, revenues, 0.0)
    
    # Calculate average revenue
    avg_revenue = total_revenue / len(revenues) if revenues else 0.0
    
    # Group by product using reduce
    product_totals = reduce(group_by_product, valid_sales, {})
    
    # Find top-selling product
    top_product = max(product_totals.items(), key=lambda x: x[1], default=('None', 0))
    
    # Example of partial application: filter sales for a specific region
    north_sales = list(filter(filter_by_region('North'), valid_sales))
    north_revenue = reduce(lambda x, y: x + calculate_revenue(y), north_sales, 0.0)
    
    return {
        'total_revenue': round(total_revenue, 2),
        'average_revenue': round(avg_revenue, 2),
        'top_product': {'product_id': top_product[0], 'total_quantity': top_product[1]},
        'north_region_revenue': round(north_revenue, 2)
    }

# Run analysis
result = analyze_sales(sales_data)

# Print results
print("Sales Analysis Results:")
print(f"Total Revenue: ${result['total_revenue']}")
print(f"Average Revenue per Sale: ${result['average_revenue']}")
print(f"Top-Selling Product: {result['top_product']['product_id']} "
      f"({result['top_product']['total_quantity']} units)")
print(f"North Region Revenue: ${result['north_region_revenue']}")

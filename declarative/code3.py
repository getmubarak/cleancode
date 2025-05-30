from functools import reduce, partial
from typing import List, Dict, Tuple
from collections import namedtuple
import operator

# Immutable data structure for student records
Student = namedtuple('Student', ['name', 'scores', 'section'])

# Sample student data
students = [
    Student('Alice', [85, 90, 95], 'A'),
    Student('Bob', [-10, 75, 80], 'B'),  # Invalid: negative score
    Student('Charlie', [70, 65, 60], 'A'),
    Student('Diana', [], 'B'),           # Invalid: empty scores
    Student('Eve', [95, 100, 90], 'A'),
    Student('Frank', [55, 60, 65], 'B'),
    Student('Grace', [92, 88, 90], 'A'),
]

# Pure function to validate a student record
def is_valid_student(student: Student) -> bool:
    return bool(student.scores) and all(score >= 0 for score in student.scores)

# Pure function to calculate average score for a student
def calculate_average_score(student: Student) -> float:
    return sum(student.scores) / len(student.scores) if student.scores else 0.0

# Pure function to check if a student passed (average score >= 60)
def is_passing_student(student: Student) -> bool:
    return calculate_average_score(student) >= 60.0

# Pure function to check if a student is a top performer (average score >= 90)
def is_top_performer(student: Student) -> bool:
    return calculate_average_score(student) >= 90.0

# Higher-order function to filter students by section
def filter_by_section(section: str) -> callable:
    return lambda student: student.section == section

# Function composition: combine two functions (e.g., filter passing students in a section)
def compose(f, g):
    """Compose two functions: f(g(x))"""
    return lambda x: f(g(x))

# Pure function to aggregate total scores and count for averaging
def aggregate_scores(acc: Tuple[float, int], student: Student) -> Tuple[float, int]:
    avg_score = calculate_average_score(student)
    return (acc[0] + avg_score, acc[1] + 1)

# Main functional pipeline
def analyze_students(students: List[Student]) -> Dict:
    # Filter valid students
    valid_students = list(filter(is_valid_student, students))
    
    # Calculate average score across all students
    total_avg_scores, count = reduce(
        aggregate_scores,
        valid_students,
        (0.0, 0)
    )
    overall_average = total_avg_scores / count if count > 0 else 0.0
    
    # Get passing students
    passing_students = list(filter(is_passing_student, valid_students))
    
    # Get top performers
    top_performers = list(filter(is_top_performer, valid_students))
    
    # Filter students in section A using partial application
    section_a_filter = partial(filter_by_section, 'A')
    section_a_students = list(filter(section_a_filter, valid_students))
    section_a_passing = list(filter(is_passing_student, section_a_students))
    
    # Example of function composition: passing students in section A
    passing_in_section_a = list(filter(
        compose(is_passing_student, section_a_filter),
        valid_students
    ))
    
    return {
        'overall_average': round(overall_average, 2),
        'passing_student_count': len(passing_students),
        'top_performers': [student.name for student in top_performers],
        'section_a_passing_count': len(section_a_passing),
        'section_a_passing_names': [student.name for student in passing_in_section_a]
    }

# Run analysis
result = analyze_students(students)

# Print results
print("Student Score Analysis Results:")
print(f"Overall Average Score: {result['overall_average']}")
print(f"Number of Passing Students: {result['passing_student_count']}")
print(f"Top Performers: {', '.join(result['top_performers']) or 'None'}")
print(f"Passing Students in Section A: {result['section_a_passing_count']}")
print(f"Names of Passing Students in Section A: {', '.join(result['section_a_passing_names']) or 'None'}")

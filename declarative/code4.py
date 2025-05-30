from typing import List, Dict

# Class to represent a student record
class Student:
    def __init__(self, name: str, scores: List[float], section: str):
        self._name = name
        self._scores = scores.copy()  # Ensure immutability by copying
        self._section = section

    # Properties for read-only access
    @property
    def name(self) -> str:
        return self._name

    @property
    def scores(self) -> List[float]:
        return self._scores.copy()  # Return copy to prevent modification

    @property
    def section(self) -> str:
        return self._section

    def is_valid(self) -> bool:
        """Check if the student record is valid."""
        return bool(self.scores) and all(score >= 0 for score in self.scores)

    def calculate_average_score(self) -> float:
        """Calculate average score for the student."""
        return sum(self.scores) / len(self.scores) if self.scores else 0.0

    def is_passing(self) -> bool:
        """Check if the student passed (average score >= 60)."""
        return self.calculate_average_score() >= 60.0

    def is_top_performer(self) -> bool:
        """Check if the student is a top performer (average score >= 90)."""
        return self.calculate_average_score() >= 90.0

# Class to analyze student data
class StudentAnalyzer:
    def __init__(self, students: List[Student]):
        self._students = students

    def get_valid_students(self) -> List[Student]:
        """Filter valid student records."""
        return [student for student in self._students if student.is_valid()]

    def calculate_overall_average(self) -> float:
        """Calculate average score across all valid students."""
        valid_students = self.get_valid_students()
        if not valid_students:
            return 0.0
        total_score = sum(student.calculate_average_score() for student in valid_students)
        return total_score / len(valid_students)

    def get_passing_students(self) -> List[Student]:
        """Get list of passing students."""
        return [student for student in self.get_valid_students() if student.is_passing()]

    def get_top_performers(self) -> List[Student]:
        """Get list of top-performing students."""
        return [student for student in self.get_valid_students() if student.is_top_performer()]

    def get_passing_students_by_section(self, section: str) -> List[Student]:
        """Get passing students in a specific section."""
        return [
            student for student in self.get_valid_students()
            if student.section == section and student.is_passing()
        ]

    def analyze(self) -> Dict:
        """Perform full student score analysis."""
        valid_students = self.get_valid_students()
        overall_average = self.calculate_overall_average()
        passing_students = self.get_passing_students()
        top_performers = self.get_top_performers()
        section_a_passing = self.get_passing_students_by_section('A')

        return {
            'overall_average': round(overall_average, 2),
            'passing_student_count': len(passing_students),
            'top_performers': [student.name for student in top_performers],
            'section_a_passing_count': len(section_a_passing),
            'section_a_passing_names': [student.name for student in section_a_passing]
        }

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

# Run analysis
analyzer = StudentAnalyzer(students)
result = analyzer.analyze()

# Print results
print("Student Score Analysis Results:")
print(f"Overall Average Score: {result['overall_average']}")
print(f"Number of Passing Students: {result['passing_student_count']}")
print(f"Top Performers: {', '.join(result['top_performers']) or 'None'}")
print(f"Passing Students in Section A: {result['section_a_passing_count']}")
print(f"Names of Passing Students in Section A: {', '.join(result['section_a_passing_names']) or 'None'}")



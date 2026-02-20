# Response validation service
from pydantic import ValidationError
from models.schemas import AIResponseModel


def validate_response(data: dict) -> dict:
    try:
        validated = AIResponseModel(**data)
        return validated.model_dump()
    except ValidationError as e:
        return {
            "status": "error",
            "message": "Schema validation failed",
            "details": e.errors()
        }
    except Exception as e:
        return {
            "status": "error",
            "message": "Unexpected validation error",
            "details": str(e)
        }
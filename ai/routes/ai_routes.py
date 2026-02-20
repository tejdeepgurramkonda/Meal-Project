# AI routes
from fastapi import APIRouter
from services.meal_ai_service import process_query
from services.response_validator import validate_response
from services.error_handler import safe_execute

router = APIRouter()


@router.post("/query")
@safe_execute
async def query_ai(payload: dict):
    raw_result = await process_query(payload["query"])
    validated_result = validate_response(raw_result)
    return validated_result
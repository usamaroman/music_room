// Package docs Code generated by swaggo/swag. DO NOT EDIT
package docs

import "github.com/swaggo/swag"

const docTemplate = `{
    "schemes": {{ marshal .Schemes }},
    "swagger": "2.0",
    "info": {
        "description": "{{escape .Description}}",
        "title": "{{.Title}}",
        "contact": {},
        "version": "{{.Version}}"
    },
    "host": "{{.Host}}",
    "basePath": "{{.BasePath}}",
    "paths": {
        "/auth/code/{userID}": {
            "post": {
                "description": "Endpoint for sending code",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "auth"
                ],
                "summary": "Send code",
                "parameters": [
                    {
                        "type": "string",
                        "description": "User ID",
                        "name": "userID",
                        "in": "path",
                        "required": true
                    }
                ],
                "responses": {
                    "204": {
                        "description": "No Content",
                        "schema": {
                            "$ref": "#/definitions/github_com_usamaroman_music_room_backend_internal_proc_response.Login"
                        }
                    }
                }
            }
        },
        "/auth/login": {
            "post": {
                "description": "Endpoint for login users",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "auth"
                ],
                "summary": "Login",
                "parameters": [
                    {
                        "description": "Login Body",
                        "name": "body",
                        "in": "body",
                        "required": true,
                        "schema": {
                            "$ref": "#/definitions/github_com_usamaroman_music_room_backend_internal_proc_request.Login"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK",
                        "schema": {
                            "$ref": "#/definitions/github_com_usamaroman_music_room_backend_internal_proc_response.Login"
                        }
                    }
                }
            }
        },
        "/auth/refresh": {
            "post": {
                "description": "Endpoint for refreshing tokens",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "auth"
                ],
                "summary": "Refresh tokens",
                "parameters": [
                    {
                        "description": "Refresh Body",
                        "name": "body",
                        "in": "body",
                        "required": true,
                        "schema": {
                            "$ref": "#/definitions/github_com_usamaroman_music_room_backend_internal_proc_request.Refresh"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK",
                        "schema": {
                            "$ref": "#/definitions/github_com_usamaroman_music_room_backend_internal_proc_response.Login"
                        }
                    }
                }
            }
        },
        "/auth/registration": {
            "post": {
                "description": "Endpoint for registration users",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "auth"
                ],
                "summary": "Registration",
                "parameters": [
                    {
                        "description": "Registration Body",
                        "name": "body",
                        "in": "body",
                        "required": true,
                        "schema": {
                            "$ref": "#/definitions/github_com_usamaroman_music_room_backend_internal_proc_request.Registration"
                        }
                    }
                ],
                "responses": {
                    "201": {
                        "description": "Created",
                        "schema": {
                            "$ref": "#/definitions/github_com_usamaroman_music_room_backend_internal_proc_response.Registration"
                        }
                    }
                }
            }
        },
        "/auth/submit": {
            "post": {
                "description": "Endpoint for submiting code",
                "consumes": [
                    "application/json"
                ],
                "produces": [
                    "application/json"
                ],
                "tags": [
                    "auth"
                ],
                "summary": "Submit code",
                "parameters": [
                    {
                        "description": "Submit code Body",
                        "name": "body",
                        "in": "body",
                        "required": true,
                        "schema": {
                            "$ref": "#/definitions/github_com_usamaroman_music_room_backend_internal_proc_request.Submit"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "OK",
                        "schema": {
                            "$ref": "#/definitions/github_com_usamaroman_music_room_backend_internal_proc_response.Submit"
                        }
                    }
                }
            }
        }
    },
    "definitions": {
        "github_com_usamaroman_music_room_backend_internal_proc_request.Login": {
            "type": "object",
            "required": [
                "email",
                "password"
            ],
            "properties": {
                "email": {
                    "type": "string"
                },
                "password": {
                    "type": "string"
                }
            }
        },
        "github_com_usamaroman_music_room_backend_internal_proc_request.Refresh": {
            "type": "object",
            "required": [
                "token"
            ],
            "properties": {
                "token": {
                    "type": "string"
                }
            }
        },
        "github_com_usamaroman_music_room_backend_internal_proc_request.Registration": {
            "type": "object",
            "required": [
                "email",
                "nickname",
                "password"
            ],
            "properties": {
                "email": {
                    "type": "string"
                },
                "nickname": {
                    "type": "string",
                    "maxLength": 30
                },
                "password": {
                    "type": "string",
                    "maxLength": 30,
                    "minLength": 6
                }
            }
        },
        "github_com_usamaroman_music_room_backend_internal_proc_request.Submit": {
            "type": "object",
            "properties": {
                "code": {
                    "type": "string"
                },
                "user_id": {
                    "type": "integer"
                }
            }
        },
        "github_com_usamaroman_music_room_backend_internal_proc_response.Login": {
            "type": "object",
            "properties": {
                "access_token": {
                    "type": "string"
                },
                "refresh_token": {
                    "type": "string"
                }
            }
        },
        "github_com_usamaroman_music_room_backend_internal_proc_response.Registration": {
            "type": "object",
            "properties": {
                "id": {
                    "type": "integer"
                }
            }
        },
        "github_com_usamaroman_music_room_backend_internal_proc_response.Submit": {
            "type": "object",
            "properties": {
                "msg": {
                    "type": "string"
                }
            }
        }
    }
}`

// SwaggerInfo holds exported Swagger Info so clients can modify it
var SwaggerInfo = &swag.Spec{
	Version:          "",
	Host:             "",
	BasePath:         "",
	Schemes:          []string{},
	Title:            "",
	Description:      "",
	InfoInstanceName: "swagger",
	SwaggerTemplate:  docTemplate,
	LeftDelim:        "{{",
	RightDelim:       "}}",
}

func init() {
	swag.Register(SwaggerInfo.InstanceName(), SwaggerInfo)
}
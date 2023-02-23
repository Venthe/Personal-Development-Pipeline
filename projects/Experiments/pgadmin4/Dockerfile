FROM docker.io/dpage/pgadmin4:6.20
USER root
RUN sed -i 's/arpa/ignore/' /venv/lib/python3.10/site-packages/email_validator/__init__.py
USER pgadmin


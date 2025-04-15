: 确保你是jdk17+
for %%F in (".\mohist-1.19.2-*-server.jar") do (
	java -jar %%F
    )
)

pause
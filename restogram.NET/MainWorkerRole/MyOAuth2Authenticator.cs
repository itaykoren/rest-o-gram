using DotNetOpenAuth.OAuth2;
using Google.Apis.Authentication;
using Google.Apis.Authentication.OAuth2.DotNetOpenAuth;
using Google.Apis.Requests;
using Google.Apis.Util;
using System;
using System.Net;
using System.Runtime.CompilerServices;

namespace restogram.NET
{
    public class MyOAuth2Authenticator : IAuthenticator, IErrorResponseHandler
    {
        private readonly Func<AssertionFlowClient, IAuthorizationState> authProvider;

        private readonly AssertionFlowClient tokenProvider;

        private bool noCaching;

        public bool NoCaching
        {
            get
            {
                return this.noCaching;
            }
            set
            {
                this.noCaching = value;
                if (this.noCaching)
                {
                    this.State = null;
                }
            }
        }

        public IAuthorizationState State
        {
            get;
            private set;
        }

        public MyOAuth2Authenticator(AssertionFlowClient tokenProvider, Func<AssertionFlowClient, IAuthorizationState> authProvider)
        {
            Utilities.ThrowIfNull(tokenProvider, "applicationName");
            Utilities.ThrowIfNull(authProvider, "authProvider");
            this.tokenProvider = tokenProvider;
            this.authProvider = authProvider;
        }

        public void ApplyAuthenticationToRequest(HttpWebRequest request)
        {
            this.LoadAccessToken();
            try
            {
                if (this.State != null && !string.IsNullOrEmpty(this.State.AccessToken))
                {
                    if (!string.IsNullOrEmpty(this.State.RefreshToken))
                    {
                        this.tokenProvider.AuthorizeRequest(request, this.State);
                    }
                    else
                    {
                        ClientBase.AuthorizeRequest(request, this.State.AccessToken);
                    }
                }
            }
            finally
            {
                if (this.NoCaching)
                {
                    this.State = null;
                }
            }
        }

        public bool CanHandleErrorResponse(WebException exception, RequestError error)
        {
            Utilities.ThrowIfNull(exception, "exception");
            if (this.State == null)
            {
                return false;
            }
            HttpWebResponse response = exception.Response as HttpWebResponse;
            if (response == null)
            {
                return false;
            }
            return response.StatusCode == HttpStatusCode.Unauthorized;
        }

        public void HandleErrorResponse(WebException exception, RequestError error, WebRequest request)
        {
            Utilities.ThrowIfNull(request, "request");
            if (!(request is HttpWebRequest))
            {
                throw new InvalidCastException(string.Concat("Expected a HttpWebRequest, but got a ", request.GetType(), " instead."));
            }
            var tClient = this.tokenProvider;
            TimeSpan? nullable = null;
            tClient.RefreshToken(this.State, nullable);
            this.ApplyAuthenticationToRequest((HttpWebRequest)request);
        }

        public void LoadAccessToken()
        {
            if (this.State == null || string.IsNullOrEmpty(this.State.AccessToken))
            {
                this.State = this.authProvider(this.tokenProvider);
            }
        }
    }
}
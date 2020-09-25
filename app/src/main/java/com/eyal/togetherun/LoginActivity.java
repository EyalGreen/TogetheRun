package com.eyal.togetherun;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.eyal.togetherun.Fragments.GetEmailFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import android.util.Log;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
	public static final int RC_SIGN_IN = 100;
	private static final String TAG = "LoginActivity";
	private Context context;
	private FirebaseAuth mAuth;
	private EditText etEmail, etPassword;
	private Button btnSignIn;
	private GoogleSignInClient mGoogleSignInClient;
	private CallbackManager mCallbackManager;
	private AuthCredential credential;

	@Override
	public void onStart() {
		super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly.
		FirebaseUser currentUser = mAuth.getCurrentUser();
		DatabaseHandler.create();
		updateUI(currentUser);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context = this;
		mAuth = FirebaseAuth.getInstance();
		this.etEmail = findViewById(R.id.etEmail);
		this.etPassword = findViewById(R.id.etPassword);
		this.btnSignIn = findViewById(R.id.btnSignIn);
		this.etEmail.addTextChangedListener(isFormValid);
		this.etPassword.addTextChangedListener(isFormValid);
		btnSignIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				anonymousAuth(etEmail.getText().toString(), etPassword.getText().toString());
			}
		});
		findViewById(R.id.btnSubmitLoginGoogle).setOnClickListener(this);
		findViewById(R.id.resetPasswordTv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GetEmailFragment fragment = new GetEmailFragment();
				fragment.setCancelable(true);
				fragment.show(getSupportFragmentManager(), "");

			}
		});
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


// Initialize Facebook Login button
		mCallbackManager = CallbackManager.Factory.create();
		LoginButton loginButton = findViewById(R.id.login_button);
		loginButton.setReadPermissions("email", "public_profile");
		loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				Log.d(TAG, "facebook:onSuccess:" + loginResult);
				Toast.makeText(context, "succses", Toast.LENGTH_SHORT).show();
				handleFacebookAccessToken(loginResult.getAccessToken());
			}

			@Override
			public void onCancel() {
				Log.d(TAG, "facebook:onCancel");
				// ...
			}

			@Override
			public void onError(FacebookException error) {
				Log.d(TAG, "facebook:onError", error);
				// ...
			}
		});
// ...


	}

	private void handleFacebookAccessToken(AccessToken token) {
		Log.d(TAG, "handleFacebookAccessToken:" + token);

		AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();
							updateUI(user);
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());
							Toast.makeText(LoginActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
							updateUI(null);
						}
					}
				});
	}

	/**
	 * Checks if a certain String is a valid Gmail address
	 *
	 * @param emailAddress - the String to check
	 * @return true - if it is a valid Gmail address, false otherwise
	 */
	private boolean isGmailAddress(String emailAddress) {
		if (emailAddress == null) return false;
		String expression = "^[\\w.+\\-]+@gmail\\.com$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	/**
	 * Checks if a certain String is a valid password
	 *
	 * @param password - the String to check
	 * @return true - if it is a valid password, false otherwise
	 */
	private boolean isValidPassword(String password) {
		String regex = "^(?=.*[0-9])"
				+ "(?=.*[a-z])(?=.*[A-Z])"
				+ "(?=.*[@#$%^&+=])"
				+ "(?=\\S+$).{8,20}$";
		Pattern p = Pattern.compile(regex);
		if (password == null) {
			return false;
		}
		Matcher m = p.matcher(password);
		return m.matches();
	}

	private void anonymousAuth(String email, String password) {
		if (! isGmailAddress(email)) {
			Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
			return;
		}
		if (! isValidPassword(password)) {
			Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
			return;
		}
		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {

						if (task.isSuccessful()) {
							Log.d(TAG, "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();
							updateUI(user);
						} else {
							Log.w(TAG, "signInWithCredential:failure", task.getException());
							try {
								throw task.getException();
							} catch (FirebaseAuthWeakPasswordException e) {
								etPassword.setError(e.getReason());
								etPassword.requestFocus();
							} catch (FirebaseAuthInvalidCredentialsException e) {
								etEmail.setError(getString(R.string.error_invalid_email));
								etEmail.requestFocus();
							} catch (FirebaseAuthUserCollisionException e) {
								etEmail.setError(getString(R.string.error_user_exists));
								etEmail.requestFocus();
							} catch (Exception e) {
								Log.e(TAG, e.getMessage());
							}
						}
					}
				});
	}

	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
		Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();
							updateUI(user);
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());

							updateUI(null);
						}

						// ...
					}
				});
	}

	//    private void setPointer() {
//        context = this;
//        mAuth = FirebaseAuth.getInstance();
//        etEmail = findViewById(R.id.etEmail);
//        etPassword = findViewById(R.id.etPassword);
//        findViewById(R.id.btnSubmitLogin).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = etEmail.getText().toString();
//                String password = etPassword.getText().toString();
//                mAuth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d("", "createUserWithEmail:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    updateUI(user);
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.w("", "createUserWithEmail:failure", task.getException());
//                                    Toast.makeText(context, "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
//                                }
//
//                                // ...
//                            }
//                        });
//            }
//        });
//
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//    }
	private void updateUI(FirebaseUser user) {

		if (user != null) {
//            Toast.makeText(context, "succses", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, user.getDisplayName() + " " + user.getEmail(), Toast.LENGTH_SHORT).show();
			startActivity(new Intent(context, MainActivity.class));

//            sendSms();
//            Toast.makeText(context, user.getEmail(), Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnSubmitLoginGoogle:
				signIn();
				break;

			// ...
		}
	}


	private void signIn() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = task.getResult(ApiException.class);
				firebaseAuthWithGoogle(account);
			} catch (ApiException e) {
				// Google Sign In failed, update UI appropriately
				Log.w(TAG, "Google sign in failed", e);
				// ...
			}
		}
	}

	private void setFormStatus(boolean valid) {
		btnSignIn.setActivated(valid);
		btnSignIn.setAlpha(valid ? 1f : 0.35f);
	}

	private TextWatcher isFormValid = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			setFormStatus(isValidPassword(etPassword.getText().toString()) && isGmailAddress(etEmail.getText().toString()));
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

}
